package messenger.jobs;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import messenger.jobs.interfaces.StatusChangeHandler;
import messenger.jobs.interfaces.BuildUpdateHandler;

public class JobsAPI {

    private static JobsAPI jobsAPI = null;
    private final Map<String, Job> jobsMap = new HashMap<String, Job>();

    private JobsAPI(Map<String, StatusChangeHandler> jobStatuHandlerMap) {
        initialize(jobStatuHandlerMap);

    }

    private void initialize(Map<String, StatusChangeHandler> jobStatuHandlerMap) {
        Set<String> jobs = jobStatuHandlerMap.keySet();
        for (String jobId : jobs) {
            jobsMap.put(jobId, new Job(jobId, jobStatuHandlerMap.get(jobId)));
        }

    }

    public static JobsAPI getInstance(Map<String, StatusChangeHandler> jobStatuHandlerMap) {
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
			String command, String jobName) throws IOException,
			InterruptedException {
		Job job = jobsMap.get(jobName);
        
        if (job == null) {
            System.out.println(jobName + " not found in " + jobsMap.keySet());
            return false;
        }
        if (command.equalsIgnoreCase(JenkinsConfiguration.BUILD)) {
            job.buildJob(buildUpdateHandler);
            return true;
        }
        else if (command.equalsIgnoreCase(JenkinsConfiguration.STATUS)) {
            buildUpdateHandler.buildJobStatusUpdated("Current status of Job: " + job.getJobId() + ": " + job.getStatus().name());
            return true;
        }
        System.out.println(command + " is not supported.");
        return false;
        // }
	}
}
