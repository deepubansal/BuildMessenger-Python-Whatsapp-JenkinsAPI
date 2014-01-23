package messenger.messaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class MessageSender implements Runnable {

    private BlockingQueue<ComposedMessage> queue;
    private Set<ComposedMessage> receipts;
    private OutputStream outputStream;
    private MessageServerContext context;

    public MessageSender(BlockingQueue<ComposedMessage> queue, OutputStream outputStream, MessageServerContext context, Set<ComposedMessage> receipts) {
        super();
        this.queue = queue;
        // this.outputStream = outputStream;
        this.context = context;
        this.receipts = receipts;
    }

    private void consume(ComposedMessage message) {
        if (!message.getMessage().equals("")) {
            String command = message.getCommand() + "\n";
            try {
                writeMessageToCommandFile(command);
                Thread.sleep(1000);
                String fileName = Configurations.TEMP_DIR + message.getFileName();
                File file = new File(fileName);
                int timeOut = 5;
                String lastStr = "";
                Map<String, String> sentMessageIds = new HashMap<String, String>();
                List<String> numbers = message.getNumbers();
                while (timeOut > 0 && !lastStr.equalsIgnoreCase("Done") && !lastStr.equalsIgnoreCase("Timed Out")) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String str = null;
                    while ((str = br.readLine()) != null) {
                        if (str.startsWith("Jid_MessageID:")) {
                            String[] split = str.split(":")[1].split("_");
                            String sent = split[0].split("@")[0];
                            sentMessageIds.put(sent, split[1]);
                        }
                        lastStr = str;
                    }
                    br.close();
                    Thread.sleep(1000);
                    timeOut--;
                }
                Thread.sleep(1000);
                if (sentMessageIds.size() < numbers.size()) {
                    Set<String> numSet = new HashSet<String>(numbers);
                    numSet.removeAll(sentMessageIds.keySet());
                    System.out.println("Message might not be sent to: " + numSet);
                }
                message.setSentMessageIds(sentMessageIds);
                receipts.add(message);
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else
			try {
				writeMessageToCommandFile("Done\n");
				context.setWhatsAppPoisoned(true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    }

	private void writeMessageToCommandFile(String command)
			throws FileNotFoundException, IOException {
		outputStream = new FileOutputStream(context.getCommanFile(), true);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
		System.out.println(command);
		bw.write(command);
		bw.flush();
		bw.close();
	}

    @Override
    public void run() {
        while (!context.isRequestedToKill() || !context.isWhatsAppPoisoned()) {
            try {
                consume(queue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Killing " + this);
    }

}