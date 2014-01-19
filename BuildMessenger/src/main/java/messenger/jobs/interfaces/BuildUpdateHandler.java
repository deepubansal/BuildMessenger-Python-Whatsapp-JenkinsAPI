package messenger.jobs.interfaces;

import messenger.controller.dto.User;

public interface BuildUpdateHandler {
    
    public void buildJobStatusUpdated(String message);

	public User getUser();
}
