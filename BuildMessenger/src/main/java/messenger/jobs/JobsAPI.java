package messenger.jobs;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import messenger.controller.dto.JobDto;
import messenger.jobs.interfaces.BuildUpdateHandler;
import messenger.jobs.interfaces.StatusChangeHandler;

public class JobsAPI {

    private static JobsAPI jobsAPI = null;
    private final Map<String, Job> jobsMap = new HashMap<String, Job>();

    private JobsAPI(Map<JobDto, StatusChangeHandler> jobStatuHandlerMap) {
        initialize(jobStatuHandlerMap);

    }

    private void initialize(Map<JobDto, StatusChangeHandler> jobStatuHandlerMap) {
        Set<JobDto> jobs = jobStatuHandlerMap.keySet();
        for (JobDto jobDto : jobs) {
            String jobId = jobDto.getJobId();
            jobsMap.put(jobId, new Job(jobId, jobDto.isParameterizedJob(), jobStatuHandlerMap.get(jobDto)));
        }

    }

    public static JobsAPI getInstance(
            Map<JobDto, StatusChangeHandler> jobStatuHandlerMap) {
        if (jobsAPI == null) {
            jobsAPI = new JobsAPI(jobStatuHandlerMap);
        }
        return jobsAPI;
    }

    public void refreshAll() {
        Collection<Job> jobs = jobsMap.values();
        for (Job job : jobs) {
            try {
                job.refreshStatus();
            } catch (Exception e) {

                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public boolean executeJob(BuildUpdateHandler buildUpdateHandler,
            String command, String jobName, String parameters)
            throws IOException, InterruptedException {
        Job job = jobsMap.get(jobName);

        if (job == null) {
            System.out.println(jobName + " not found in " + jobsMap.keySet());
            return false;
        }
        if (command.equalsIgnoreCase(JenkinsConfiguration.BUILD)) {
            Map<String, String> params = new HashMap<String, String>();
            if (parameters != null) {
                try {
                    String[] split = parameters.split(",");
                    for (int i = 0; i < split.length; i++) {
                        String pair = split[i];
                        String[] pairArray = pair.split("=");
                        params.put(pairArray[0], pairArray[1]);
                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException(
                            "Invalid Parameters: "
                                    + parameters
                                    + ". Parameters should be of the form ==> k1=v1,k2=v2,k3=v3");
                }
            }
            job.buildJob(buildUpdateHandler, params);
            return true;
        } else if (command.equalsIgnoreCase(JenkinsConfiguration.STATUS)) {
            buildUpdateHandler.buildJobStatusUpdated("Current status of Job: "
                    + job.getJobId() + ": " + job.getStatus().name());
            return true;
        }
        System.out.println(command + " is not supported.");
        return false;
        // }
    }
}
