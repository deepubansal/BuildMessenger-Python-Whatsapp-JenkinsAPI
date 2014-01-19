package messenger.jobs;

import static messenger.util.HttpUtil.getResponse;

import java.io.IOException;
import java.util.Date;

import messenger.jobs.interfaces.BuildUpdateHandler;
import messenger.jobs.interfaces.StatusChangeEvent;
import messenger.jobs.interfaces.StatusChangeHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.sun.jndi.toolkit.url.UrlUtil;

public class Job {

    private String jobId;
    private String output;
    private JobStatus status = JobStatus.UNKNOWN;
    private Date lastRefreshTime;
    private Date lastSuccessStatusTime;
    private boolean isFailedJob = false;
    
//    private List<String> jobHistory = new LimitedArrayList<String>(Arrays.asList(new String[] { "" + (new Date()).getTime() + "," + status.name() }),
//            20);
    private StatusChangeHandler statusChangeHandler;

    public Job(String jobId, StatusChangeHandler statusChangeHandler) {
        this.jobId = jobId;
        this.statusChangeHandler = statusChangeHandler;
    }

    public void refreshStatus() throws IOException {
        String statusUrl = JenkinsConfiguration.getStatusUrl(UrlUtil.encode(jobId, "UTF-8"));
        refreshStatus(statusUrl);
    }

    private void refreshStatus(String statusUrl) throws IOException {
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
//        System.out.println("Status updated from " + prevStatus.name() + " to " + status.name() + " at: " + lastRefreshTime);
    }

    private void getStatusFromJenkins(String statusUrl) throws IOException {
        String stringStatus = getResponse(statusUrl);
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
//        String prevStatusEntry = jobHistory.get(jobHistory.size() - 1);
//        Date prevStatusDate = new Date(Long.valueOf(prevStatusEntry.split(",")[0]));
        String message = null;
        if (status.equals(JobStatus.FAILURE)) {
            message = jobId + ": Failed on " + lastRefreshTime + ".  Last Success time: " + lastSuccessStatusTime;
        }
        else {
            message = jobId + ": Back to stable on " + lastRefreshTime + ".";
        }
        StatusChangeEvent changeEvent = new StatusChangeEventImpl(jobId, null, status.name(), lastSuccessStatusTime, lastRefreshTime, message);
        statusChangeHandler.statusChanged(changeEvent);
//        jobHistory.add(lastRefreshTime.getTime() + "," + status.name());
    }

    protected void buildJob(BuildUpdateHandler buildUpdateHandler) throws IOException, InterruptedException {
        refreshStatus();
        if (getStatus().equals(JobStatus.PROGRESS)) {
            throw new IllegalStateException(jobId + " is already in progress");
        }
        else {
            String buildUrl = JenkinsConfiguration.getBuildUrl(UrlUtil.encode(jobId, "UTF-8"));
            HttpClient client = HttpClientBuilder.create().build();
            HttpUriRequest method = new HttpPost(buildUrl);
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            String resp = EntityUtils.toString(response.getEntity());
            if (statusCode != 201) {
                throw new IllegalStateException("Jenkins Server returned error Code: " + statusCode + " and Response:" + resp.substring(0, 1024));
            }
            else {
                Header responseHeader = response.getFirstHeader("Location");
                System.out.println();
                BuildListener buildListener = new BuildListener(responseHeader.getValue().trim(), buildUpdateHandler, this);
                this.statusChangeHandler.removeUser(buildUpdateHandler.getUser());
                Thread listener = new Thread(buildListener);
                listener.start();
                listener.join();
                this.statusChangeHandler.addUser(buildUpdateHandler.getUser());
            }
        }

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
