package messenger.jobs;

import static messenger.util.HttpUtil.getResponse;
import messenger.jobs.interfaces.BuildUpdateHandler;

public class BuildListener implements Runnable {
    private BuildUpdateHandler buildUpdateHandler;
    private String location;
    private Job parentJob;

    enum BuildPhase {
        TRIGGERED, WAITING, LEFTQUEUE, RUNNING, COMPLETED, UNKNOWN
    };

    public BuildListener(String location, BuildUpdateHandler buildUpdateHandler, Job parentJob) {
        this.location = location;
        this.buildUpdateHandler = buildUpdateHandler;
        this.parentJob = parentJob;
    }

    @Override
    public void run() {
        BuildPhase buildphase = BuildPhase.TRIGGERED;
        try {
            while (buildphase != BuildPhase.LEFTQUEUE) {
                int failedAttempts = 0;
                String response2 = getResponse(location + "api/xml");// ?xpath=leftItem/task/color/text()");
                System.out.println(response2);
                if (checkWaiting(response2))
                    buildphase = BuildPhase.WAITING;
                else if (checkLeftQueue(response2))
                    buildphase = BuildPhase.LEFTQUEUE;
                else {
                    buildphase = BuildPhase.UNKNOWN;
                    failedAttempts++;
                    if (failedAttempts > 4) {
//                        parentJob.refreshStatus();
                        buildUpdateHandler.buildJobStatusUpdated("Some error occurred. Current job status is " + parentJob.getStatus());
                        throw new IllegalStateException("Invalid response recieved from" + location + "api/xml. Response: " + response2);
                    }
                }
                Thread.sleep(1000);
            }
            while (buildphase != BuildPhase.COMPLETED) {
//                parentJob.refreshStatus();
                if (parentJob.getStatus() == JobStatus.SUCCESS || parentJob.getStatus() == JobStatus.FAILURE) {
                    buildphase = BuildPhase.COMPLETED;
                }
                else if (parentJob.getStatus() == JobStatus.PROGRESS) {
                    if (buildphase != BuildPhase.RUNNING)
                        buildUpdateHandler.buildJobStatusUpdated("Build started. Current status: " + parentJob.getStatus());
                    buildphase = BuildPhase.RUNNING;
                }
                Thread.sleep(2000);
            }
            buildUpdateHandler.buildJobStatusUpdated("Build Completed: " + parentJob.getStatus());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkLeftQueue(String response) {
        return response.startsWith("<leftItem>");
    }

    private boolean checkWaiting(String response) {
        return response.startsWith("<waitingItem>");
    }

}
