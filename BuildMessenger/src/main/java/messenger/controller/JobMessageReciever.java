package messenger.controller;

import java.util.Set;

import messenger.controller.configurations.Configurations;
import messenger.controller.dto.JobDto;
import messenger.controller.dto.User;
import messenger.jobs.JenkinsConfiguration;
import messenger.jobs.JobsAPI;
import messenger.jobs.interfaces.BuildUpdateHandler;
import messenger.messaging.MessageAPI;
import messenger.messaging.interfaces.MessageReciever;

public class JobMessageReciever implements MessageReciever {

	private JobsAPI jobsAPI;
	private MessageAPI messageAPI;

	public JobMessageReciever(JobsAPI jobsAPI, MessageAPI messageAPI) {
		this.jobsAPI = jobsAPI;
		this.messageAPI = messageAPI;
	}

	@Override
	public void commandRecieved(String from, String message, String messageId) {
		User user = Configurations.getUser(from);
		message = message.trim();
		String originalMessage = message;
		// System.out.println("Received message from " + user.getName() +
		// " (" + from + "): " + message);
		BuildUpdateHandler buildUpdateHandler = new BuildUpdateHandlerImpl(
				messageAPI, user);
		if (!message.contains(":")) {
			message += ":" + Configurations.getDefaultJob(user);
		}
		try {
			String[] split = message.split(":");
			String command = split[0].trim();
			String jobName = split[1].trim();
			Set<JobDto> jobList = user.getRegisteredJobs();
			JobDto jobDto = new JobDto();
			jobDto.setJobId(jobName);
			if (!jobList.contains(jobDto))
				sendError(originalMessage, buildUpdateHandler, jobList, user);
			else if (!jobsAPI.executeJob(buildUpdateHandler, command, jobName))
				sendError(originalMessage, buildUpdateHandler, jobList, user);
		} catch (Exception e) {
			buildUpdateHandler.buildJobStatusUpdated("Error occurred: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendError(String originalMessage,
			BuildUpdateHandler buildUpdateHandler, Set<JobDto> jobList, User user) {
		StringBuffer reply = new StringBuffer();
		reply.append("Invalid command:" + originalMessage);
		reply.append("\nSupported commands: \n");
		reply.append("\"" + JenkinsConfiguration.BUILD + ":<JobName>\"\n");
		reply.append("\"" + JenkinsConfiguration.STATUS + ":<JobName>\"\n");
		reply.append("Where JobNames can be:\n");
		reply.append(jobList);
		reply.append("\nDefault:" + Configurations.getDefaultJob(user));
		buildUpdateHandler.buildJobStatusUpdated(reply.toString());
	}

}
