package messenger.jobs;

import static messenger.util.HttpUtil.getResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import messenger.jobs.interfaces.BuildUpdateHandler;
import messenger.jobs.interfaces.StatusChangeEvent;
import messenger.jobs.interfaces.StatusChangeHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import com.sun.jndi.toolkit.url.UrlUtil;

public class Job {

    private String jobId;
    private String output;
    private JobStatus status = JobStatus.UNKNOWN;
    private Date lastRefreshTime;
    private Date lastSuccessStatusTime;
    private boolean isFailedJob = false;
    private boolean isParameterizedJob = false;

    // private List<String> jobHistory = new
    // LimitedArrayList<String>(Arrays.asList(new String[] { "" + (new
    // Date()).getTime() + "," + status.name() }),
    // 20);
    private StatusChangeHandler statusChangeHandler;

    public Job(String jobId, boolean isParameterizedJob, StatusChangeHandler statusChangeHandler) {
        this.jobId = jobId;
        this.statusChangeHandler = statusChangeHandler;
        this.isParameterizedJob = isParameterizedJob;
    }

    public void refreshStatus() throws IOException {
        String[] statusUrl = JenkinsConfiguration.getStatusUrls(UrlUtil.encode(jobId, "UTF-8"));
        refreshStatus(statusUrl);
    }

    private void refreshStatus(String[] statusUrl) throws IOException {
        getStatusFromJenkins(statusUrl);
        lastRefreshTime = new Date();
        if (status.equals(JobStatus.FAILURE) && !isFailedJob) {
            statusChanged();
            isFailedJob = true;
        }
        if (status.equals(JobStatus.SUCCESS) && isFailedJob) {
            statusChanged();
            isFailedJob = false;
        }
        // System.out.println("Status updated from " + prevStatus.name() +
        // " to " + status.name() + " at: " + lastRefreshTime);
    }

    private void getStatusFromJenkins(String[] statusUrl) throws IOException {
        String stringStatus = getResponse(statusUrl, this.jobId);
        stringStatus = stringStatus.replaceAll("<color>", "").replaceAll("</color>", "");
        System.out.println(stringStatus);
        if (stringStatus.equals("blue_anime"))
            status = JobStatus.PROGRESS;
        else if (stringStatus.equals("red_anime"))
            status = JobStatus.PROGRESS;
        else if (stringStatus.equals("red"))
            status = JobStatus.FAILURE;
        else if (stringStatus.equals("yellow"))
            status = JobStatus.FAILURE;
        else if (stringStatus.equals("blue")) {
            status = JobStatus.SUCCESS;
            lastSuccessStatusTime = new Date();
        }
        else
            status = JobStatus.UNKNOWN;
    }

    private void statusChanged() {
        // String prevStatusEntry = jobHistory.get(jobHistory.size() - 1);
        // Date prevStatusDate = new
        // Date(Long.valueOf(prevStatusEntry.split(",")[0]));
        String message = null;
        if (status.equals(JobStatus.FAILURE)) {
            message = jobId + ": Failed on " + lastRefreshTime + ".  Last Success time: " + lastSuccessStatusTime;
        }
        else {
            message = jobId + ": Back to stable on " + lastRefreshTime + ".";
        }
        StatusChangeEvent changeEvent = new StatusChangeEventImpl(jobId, null, status.name(), lastSuccessStatusTime, lastRefreshTime, message);
        statusChangeHandler.statusChanged(changeEvent);
        // jobHistory.add(lastRefreshTime.getTime() + "," + status.name());
    }

    // public static void main(String[] args) {
    // String resp = "";
    // System.out.println(resp.substring(0, resp.length() > 1024 ? 1024:
    // resp.length()));
    // }

    protected void buildJob(BuildUpdateHandler buildUpdateHandler, Map<String, String> params) throws IOException, InterruptedException {
        refreshStatus();
        if (getStatus().equals(JobStatus.PROGRESS)) {
            throw new IllegalStateException(jobId + " is already in progress");
        }
        else {
            String buildUrl = null;
            if (!isParameterizedJob)
                buildUrl = JenkinsConfiguration.getBuildUrl(UrlUtil.encode(jobId, "UTF-8"));
            else
                buildUrl = JenkinsConfiguration.getBuildWithParametersUrl(UrlUtil.encode(jobId, "UTF-8"), params);
            System.out.println(buildUrl);
            HttpClient client = HttpClientBuilder.create().build();
            HttpUriRequest method = new HttpPost(buildUrl);
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode / 100 != 2 && statusCode / 100 != 3) {
                // String resp = EntityUtils.toString(response.getEntity());
                throw new IllegalStateException("Jenkins Server returned error Code: " + statusCode);
            }
            else {
                if (statusCode == 201) {
                    Header responseHeader = response.getFirstHeader("Location");
                    System.out.println(responseHeader.getValue());
                    BuildListener buildListener = new BuildListener(responseHeader.getValue().trim(), buildUpdateHandler, this);
                    this.statusChangeHandler.removeUser(buildUpdateHandler.getUser());
                    Thread listener = new Thread(buildListener);
                    listener.start();
                    listener.join();
                    this.statusChangeHandler.addUser(buildUpdateHandler.getUser());
                }
                else {
                    buildUpdateHandler
                            .buildJobStatusUpdated("Build started. But no build number returned from jenkins. You can check the status by sending \"Status:<Job Name>\" command."
                                    + "Jenkins Server returned Code: " + statusCode);
                    
                }
            }
        }

    }

    protected void buildJob(BuildUpdateHandler buildUpdateHandler) throws IOException, InterruptedException {
        buildJob(buildUpdateHandler, null);
    }

    public String getJobId() {
        return jobId;
    }

    public String getOutput() {
        return output;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public Date getLastSuccessStatusTime() {
        return lastSuccessStatusTime;
    }

}
