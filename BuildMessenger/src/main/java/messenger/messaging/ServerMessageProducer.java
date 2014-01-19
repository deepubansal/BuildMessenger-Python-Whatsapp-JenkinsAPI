package messenger.messaging;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class ServerMessageProducer implements Runnable {

    private BlockingQueue<String> queue;
    private InputStream inputStream;
    private MessageServerContext context;

    public ServerMessageProducer(BlockingQueue<String> queue, InputStream inputStream, MessageServerContext context) {
        super();
        this.queue = queue;
        this.inputStream = inputStream;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while (!context.isRequestedToKill() && !Thread.interrupted()) {
                if (br.ready()) {
                    str = br.readLine();
                    System.out.println("MessageFromServer:" + str);
                    queue.put(str);
//                    System.out.println("Producer:" + queue);
                }
            }
            br.close();
            queue.put("");
            System.out.println("Killing " + this);
        } catch (Exception e) {
            e.printStackTrace();
            new RuntimeException(e);
        }
    }

}
