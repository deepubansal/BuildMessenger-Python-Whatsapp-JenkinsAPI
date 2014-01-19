package messenger.messaging;

public interface MessageListener {

	public void messageRecieved(String from, String message, String messageId);
}
