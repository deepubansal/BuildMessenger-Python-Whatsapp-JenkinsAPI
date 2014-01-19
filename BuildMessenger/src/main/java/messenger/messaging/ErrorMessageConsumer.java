package messenger.messaging;

import java.util.concurrent.BlockingQueue;

public class ErrorMessageConsumer extends ServerMessageConsumer {

    public ErrorMessageConsumer(BlockingQueue<String> queue, MessageServerContext context) {
        super(queue, context);
    }

    @Override
    protected void consume(String message) {
        if (!message.equals("")) {
            System.out.println("Error Occurred:" + message);
        }
    }

}
