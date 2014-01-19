package messenger.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messenger.controller.configurations.Configurations;
import messenger.controller.dto.JobDto;
import messenger.controller.dto.JobsDto;
import messenger.controller.dto.User;
import messenger.jobs.JobsAPI;
import messenger.jobs.interfaces.StatusChangeHandler;
import messenger.messaging.MessageAPI;
import messenger.messaging.interfaces.MessageReciever;

public class Controller {
    public static void main(String[] args) throws InterruptedException {
        MessageAPI messageAPI = MessageAPI.getInstance();
        Runtime.getRuntime().addShutdownHook(messageAPI);
        JobsDto jobsDto = Configurations.getJobs();
        List<JobDto> jobList = jobsDto.getJobDtos();
        Map<String, StatusChangeHandler> jobStatusChangeHandlerMap = populateJobStatusChangeHandlers(messageAPI, jobList);
        JobsAPI jobsAPI = JobsAPI.getInstance(jobStatusChangeHandlerMap);
        MessageReciever messageReciever = new JobMessageReciever(jobsAPI, messageAPI);
        messageAPI.registerMessageReciever(messageReciever);
        int timeout = 25;
        while (!messageAPI.isListening()) {
            Thread.sleep(1000);
            if (--timeout <= 0)
            {
                System.err.println("Could not start Message Processor");
                messageAPI.killServer();
                return;
            }
        }
        try {
            while (messageAPI.isListening()) {
                jobsAPI.refreshAll();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            messageAPI.killServer();
        }
    }

    private static Map<String, StatusChangeHandler> populateJobStatusChangeHandlers(MessageAPI messageAPI, List<JobDto> jobList) {
        Map<String, StatusChangeHandler> jobStatusHandlerMap = new HashMap<String, StatusChangeHandler>();
        for (JobDto jobDto : jobList) {
            List<User> users = Configurations.getRegisteredUsers(jobDto);
            if (users != null && !users.isEmpty())
                jobStatusHandlerMap.put(jobDto.getJobId(), new StatusChangeHandlerImpl(users, messageAPI));
        }
        return jobStatusHandlerMap;
    }

}
