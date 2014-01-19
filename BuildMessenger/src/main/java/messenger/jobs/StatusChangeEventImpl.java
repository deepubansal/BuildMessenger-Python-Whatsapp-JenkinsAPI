package messenger.jobs;

import java.util.Date;

import messenger.jobs.interfaces.StatusChangeEvent;

public class StatusChangeEventImpl implements StatusChangeEvent {
    private String from;
    private String to;
    private Date prevStatusTime;
    private Date currStatusTime;
    private String jobId;
    private String changeMessage;

    public StatusChangeEventImpl(String jobId, String from, String to, Date prevStatusTime, Date currStatusTime, String changeMessage) {
        super();
        this.jobId = jobId;
        this.from = from;
        this.to = to;
        this.prevStatusTime = prevStatusTime;
        this.currStatusTime = currStatusTime;
        this.changeMessage = changeMessage;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public Date getPrevStatusTime() {
        return prevStatusTime;
    }


    @Override
    public Date getCurrStatusTime() {
        return currStatusTime;
    }

    @Override
    public String getChangeMessage() {
        return changeMessage;
    }
}
