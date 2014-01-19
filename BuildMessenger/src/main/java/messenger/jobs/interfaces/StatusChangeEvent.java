package messenger.jobs.interfaces;

import java.util.Date;

public interface StatusChangeEvent {

    public abstract String getFrom();

    public abstract String getJobId();

    public abstract String getTo();

    public abstract Date getPrevStatusTime();

    public abstract Date getCurrStatusTime();
    
    public abstract String getChangeMessage();

}