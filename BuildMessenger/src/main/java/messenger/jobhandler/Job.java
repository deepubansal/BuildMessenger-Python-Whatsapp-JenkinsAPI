package messenger.jobhandler;

import java.io.IOException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

    public void refreshStatus() throws IOException {
        String prevStatus = status.name();
        String stringStatus = getJobStatus();
        System.out.println(stringStatus);
        if (stringStatus.equals("blue_anime"))
            status = JobStatus.PROGRESS;
        else if (stringStatus.equals("red_anime"))
            status = JobStatus.PROGRESS;
        else if (stringStatus.equals("red"))
            status = JobStatus.FAILURE;
        else if (stringStatus.equals("yellow"))
            status = JobStatus.FAILURE;
        else if (stringStatus.equals("blue"))
        {
            status = JobStatus.SUCCESS;
            lastSuccessStatusTime = new Date();
        }
        else 
            status = JobStatus.UNKNOWN;
        lastRefreshTime = new Date();
        System.out.println("Status updated from " + prevStatus + " to " + status.name() + " at: " + lastRefreshTime);
    }

    private String getJobStatus() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        String statusUrl = JenkinsConfiguration.getStatusUrl(UrlUtil.encode(jobId, "UTF-8"));
        System.out.println(statusUrl);
        HttpUriRequest request = new HttpGet(statusUrl);
        HttpResponse httpResponse = client.execute(request);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public void buildJob() throws IOException {
        refreshStatus();
        if (getStatus().equals(JobStatus.PROGRESS)) {
            throw new IllegalStateException(jobId + " is already in progress");
        }
        else {
            HttpClient client = HttpClientBuilder.create().build();
            HttpUriRequest method = new HttpPost(JenkinsConfiguration.getBuildUrl(UrlUtil.encode(jobId, "UTF-8")));
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            String resp = EntityUtils.toString(response.getEntity());
            if (statusCode != 201) {
                throw new IllegalStateException("Jenkins Server returned error Code: " + statusCode + " and Response:" + resp.substring(0, 1024));
            }
            else {
                Header responseHeader = response.getFirstHeader("Location");
                System.out.println(responseHeader.toString());
            }
        }
        refreshStatus();
    }

    public static void main(String[] args) throws IOException {
        (new Job("Build Local")).buildJob();
    }

    public Job(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
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
