package messenger.controller.dto;

import javax.xml.bind.annotation.XmlElement;

public class JobDto {

    private String jobId;
    
    private boolean isParameterizedJob;

    @XmlElement(name = "JobId")
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }


    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return this.jobId.length();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JobDto) {
            JobDto jobObject = (JobDto) obj;
            return jobObject.getJobId().equalsIgnoreCase(this.getJobId());
        }
        return false;
    }
    // public void setName(String name) {
    // this.name = name;
    // }
    // public void setNumber(String number) {
    // this.number = number;
    // }
    //
    //
    //
    // @XmlElement(name="Name")
    // public String getName() {
    // return name;
    // }
    //
    // @XmlElement(name="Number")
    // public String getNumber() {
    // return number;
    // }
    @Override
    public String toString() {
        return jobId;
    }

    @XmlElement(name = "IsParameterizedJob")
    public boolean isParameterizedJob() {
        return isParameterizedJob;
    }

    public void setParameterizedJob(boolean isParameterizedJob) {
        this.isParameterizedJob = isParameterizedJob;
    }
}
