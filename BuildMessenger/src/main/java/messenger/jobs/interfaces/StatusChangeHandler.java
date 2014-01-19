package messenger.jobs.interfaces;

import messenger.controller.dto.User;


public interface StatusChangeHandler {
    public void statusChanged(StatusChangeEvent statusChangeEventImpl);
    public void removeUser(User user);
    public void addUser(User user);
    
}
