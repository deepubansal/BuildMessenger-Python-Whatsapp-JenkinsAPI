package messenger.dto;

import javax.xml.bind.annotation.XmlElement;

public class JobDto {

    private String jobId;

    @XmlElement(name = "JobId")
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
}
