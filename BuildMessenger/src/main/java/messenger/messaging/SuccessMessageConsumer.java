package messenger.messaging;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import messenger.messaging.interfaces.MessageReciever;
import messenger.util.FileUtil;

public class SuccessMessageConsumer extends ServerMessageConsumer {

    private List<MessageReciever> messageRecievers;
    private MessageServerContext context;

    public SuccessMessageConsumer(List<MessageReciever> messageRecievers, BlockingQueue<String> queue, MessageServerContext context) {
        super(queue, context);
        this.messageRecievers = messageRecievers;
        this.context = context;
    }

    @Override
    protected void consume(String rawMessage) {
//        System.out.println("Rawmessage:" + rawMessage);
        if (!rawMessage.equals("")) {
            if (rawMessage.startsWith("Disconnected because")) {
                context.setAuthed(false);
                context.setServerUp(false);
                throw new RuntimeException("Disconnected:" + rawMessage);
            }
            if (!context.isAuthed()) {
                if (rawMessage.contains("Authed")) {
                    context.setAuthed(true);
                    System.out.println("Message Lisetner started.");
                }
                return;
            }
            String fileName = rawMessage.substring(rawMessage.lastIndexOf('\\') + 1);
            System.out.println(fileName);
            String[] split = fileName.split("_");
            String from = split[1];
            String messageId = split[2];
            String message;
            try {
                message = FileUtil.readFile(rawMessage);
                FileUtil.deleteFile(rawMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (MessageReciever messageReciver : messageRecievers) {
                messageReciver.commandRecieved(from, message, messageId);
            }
        }
    }
}
