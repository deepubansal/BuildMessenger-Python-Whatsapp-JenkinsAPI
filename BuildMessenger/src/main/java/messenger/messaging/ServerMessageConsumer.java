package messenger.messaging;

import java.util.concurrent.BlockingQueue;

public abstract class ServerMessageConsumer implements Runnable {

    protected BlockingQueue<String> queue;
    private MessageServerContext context;

    public ServerMessageConsumer(BlockingQueue<String> queue, MessageServerContext context) {
        super();
        this.queue = queue;
        this.context = context;
    }

    protected abstract void consume(String message);

    @Override
    public void run() {
        while (!context.isRequestedToKill()) {
            try {
//                System.out.println("Consumer:"+queue);
                String take = queue.take();
//                System.out.println("Take" + take);
                consume(take);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        System.out.println("Killing " + this);
    }

}