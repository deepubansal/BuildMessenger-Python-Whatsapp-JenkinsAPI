package messenger.messaging.interfaces;

public interface MessageReciever {

	public void commandRecieved(String from, String message, String messageId);
}
