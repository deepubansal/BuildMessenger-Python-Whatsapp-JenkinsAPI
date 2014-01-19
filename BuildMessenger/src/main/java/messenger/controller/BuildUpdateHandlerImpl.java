package messenger.controller;

import java.util.Arrays;

import messenger.controller.dto.User;
import messenger.jobs.interfaces.BuildUpdateHandler;
import messenger.messaging.MessageAPI;

public class BuildUpdateHandlerImpl implements BuildUpdateHandler{

    private User user;
    private MessageAPI messageAPI;

    public BuildUpdateHandlerImpl(MessageAPI messageAPI, User user) {
        this.user = user;
        this.messageAPI = messageAPI;
    }

    @Override
    public void buildJobStatusUpdated(String message) {
        System.out.println("Sending Message:\"" + message+ "\" to " + user.getName());
        try {
            messageAPI.sendMessage(Arrays.asList(new String[]{user.getNumber()}), message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
	public User getUser() {
		return user;
	}
    
    

}
