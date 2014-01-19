package messenger.controller.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

public class User {

    private String number;
    private String name;
    private Set<JobDto> registeredJobs;

    public void setName(String name) {
        this.name = name;
    }

    public void setRegisteredJobs(Set<JobDto> registeredJobs) {
        this.registeredJobs = registeredJobs;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    @XmlElement(name = "Number")
    public String getNumber() {
        return number;
    }

    @XmlElement(name = "RegisteredJob")
    public Set<JobDto> getRegisteredJobs() {
        return registeredJobs;
    }
}
