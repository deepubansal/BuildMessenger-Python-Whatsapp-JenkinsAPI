package messenger.controller;

import java.util.ArrayList;
import java.util.List;

import messenger.controller.dto.User;
import messenger.jobs.interfaces.StatusChangeEvent;
import messenger.jobs.interfaces.StatusChangeHandler;
import messenger.messaging.MessageAPI;

public class StatusChangeHandlerImpl implements StatusChangeHandler {

    private List<User> users;
    private MessageAPI messageAPI;

    public StatusChangeHandlerImpl(List<User> users, MessageAPI messageAPI) {
        super();
        this.users = users;
        this.messageAPI = messageAPI;
    }

    @Override
    public void statusChanged(StatusChangeEvent statusChangeEventImpl) {
        List<String> numbers = new ArrayList<String>();
        if (users.size() == 0)
        	return;
        for (User user : users) {
            numbers.add(user.getNumber());
        }
        String message = statusChangeEventImpl.getChangeMessage();
        System.out.println("Sending message: " + message + " To "+ numbers);
        try {
            messageAPI.sendMessage(numbers, message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

	@Override
	public void removeUser(User user) {
		this.users.remove(user);
	}

	@Override
	public void addUser(User user) {
		this.users.add(user);
	}
    
    
}
