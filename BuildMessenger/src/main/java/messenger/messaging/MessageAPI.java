package messenger.messaging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import messenger.messaging.interfaces.MessageReciever;
import messenger.util.FileUtil;

public class MessageAPI extends Thread{

    private static MessageAPI messageAPI;

    private final List<MessageReciever> messageRecievers = new ArrayList<MessageReciever>();
    private final BlockingQueue<String> incomingMessageQueue;
    private final BlockingQueue<ComposedMessage> outgoingMessageQueue;
    private final Set<ComposedMessage> receipts;
    private final BlockingQueue<String> errorMessageQueue;
    private final MessageServerContext context;
    private final ServerMessageProducer successMessageProducer;
    private final ServerMessageProducer errorMessageProducer;
    private final ServerMessageConsumer successMessageConsumer;
    private final ServerMessageConsumer errorMessageConsumer;
    private final MessageSender messageSender;
    private final AtomicInteger fileNameInteger;
    private Process whatsAppClient;

    private MessageAPI() {
        incomingMessageQueue = new LinkedBlockingQueue<String>();
        outgoingMessageQueue = new LinkedBlockingQueue<ComposedMessage>();
        errorMessageQueue = new LinkedBlockingQueue<String>();
        receipts = new HashSet<ComposedMessage>();
        context = new MessageServerContext();
        OutputStream outputStream;
        try {
            
            whatsAppClient = ProcessFactory.getProcessMessageListener();
            outputStream = new FileOutputStream(context.getCommanFile());
        } catch (IOException e) {
            System.out.println("Could Not start WhatsApp Client");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        context.setServerUp(true);
        successMessageProducer = new ServerMessageProducer(incomingMessageQueue, whatsAppClient.getInputStream(), context);
        errorMessageProducer = new ServerMessageProducer(errorMessageQueue, whatsAppClient.getErrorStream(), context);
        successMessageConsumer = new SuccessMessageConsumer(messageRecievers, incomingMessageQueue, context);
        errorMessageConsumer = new ErrorMessageConsumer(errorMessageQueue, context);
        messageSender = new MessageSender(outgoingMessageQueue, outputStream, context, receipts);
        fileNameInteger = new AtomicInteger();
        startThreads();
    }

    private void startThreads() {
        new Thread(successMessageProducer).start();
        new Thread(errorMessageProducer).start();
        new Thread(successMessageConsumer).start();
        new Thread(errorMessageConsumer).start();
        new Thread(messageSender).start();
    }

    public void registerMessageReciever(MessageReciever messageReciever) {
        messageRecievers.add(messageReciever);
    }

    public static synchronized MessageAPI getInstance() {
        if (messageAPI == null) {
            messageAPI = new MessageAPI();
        }
        return messageAPI;
    }

    public void killServer() throws InterruptedException {
        context.requestKill();
        outgoingMessageQueue.put(new ComposedMessage("", "", null));
    }

    public boolean isListening() {
        return context.isServerListening();
    }

    public ComposedMessage sendMessage(List<String> numbers, String message) throws IOException, InterruptedException {
        String fileName = "" + fileNameInteger.incrementAndGet();
        ComposedMessage composedMessage = new ComposedMessage(fileName, message, numbers);
        fileName = Configurations.TEMP_DIR + fileName;

        FileUtil.writeToFile(fileName, message);
        outgoingMessageQueue.put(composedMessage);
        while (!receipts.contains(composedMessage))
            Thread.sleep(100);
        return composedMessage;
    }
    @Override
    public void run() {
    	System.out.println("Destroying whatsapp Client");
//    	whatsAppClient.destroy();
    	try {
    		killServer();
			whatsAppClient.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			whatsAppClient.destroy();
		}
    }
}
