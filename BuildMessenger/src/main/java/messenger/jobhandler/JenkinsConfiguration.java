package messenger.jobhandler;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

public class JenkinsConfiguration {

    // private static final String buildURL =
    // "http://localhost:8181/jenkins/job/Build%20Local/build";
    // private static final String statusURL =
    // "http://localhost:8181/jenkins/api/xml?xpath=hudson/job[name=\"Build Local\"]/color/text()";
    private static final String buildURL;
    private static final String statusURL;
    private static Properties properties = null;

    static {
        URL url = ClassLoader.getSystemResource("Urls.properties");
        try {
            properties = new XProperties();
            properties.load(new FileInputStream(new File(url.getFile())));
            buildURL = properties.getProperty("build.url");
            statusURL = properties.getProperty("status.url");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
       
    }

    public static final String BUILD = "Build";
    public static final String STATUS = "Status";

    public static String getBuildUrl(String jobId) {
        return MessageFormat.format(buildURL, jobId);
    }

    public static String getStatusUrl(String jobId) {
        return MessageFormat.format(statusURL, jobId);
    }

}
