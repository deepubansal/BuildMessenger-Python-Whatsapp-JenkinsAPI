package messenger.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SupportedJobs")
public class JobsDto{

	List<JobDto> jobDtos;
	
	@XmlElement(name="Job")
	public List<JobDto> getJobDtos() {
		return jobDtos;
	}


	public void setJobDtos(List<JobDto> jobDtos) {
		this.jobDtos = jobDtos;
	}
}
