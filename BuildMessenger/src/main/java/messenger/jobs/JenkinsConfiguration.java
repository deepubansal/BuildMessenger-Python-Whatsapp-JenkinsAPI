package messenger.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import messenger.util.XProperties;

public class JenkinsConfiguration
{

    // private static final String buildURL =
    // "http://localhost:8181/jenkins/job/Build%20Local/build";
    // private static final String statusURL =
    // "http://localhost:8181/jenkins/api/xml?xpath=hudson/job[name=\"Build Local\"]/color/text()";
    private static final String buildURL;
    private static final String buildWithParametersURL;
    private static final String statusURL;
    private static final String statusWebURL;
    private static Properties properties = null;
    private final static String CONFIG_DIR = new File("../resources/config/").getAbsolutePath();

    static {
        try {
            properties = new XProperties();
            properties.load(new FileInputStream(new File(CONFIG_DIR + "/Urls.properties")));
            buildURL = properties.getProperty("build.url");
            buildWithParametersURL = properties.getProperty("buildWithParameters.url");
            statusURL = properties.getProperty("status.url");
            statusWebURL = properties.getProperty("statusWeb.url");
            minRefreshInterval = Integer.valueOf(properties.getProperty("minRefreshInterval")) * 1000L;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static final String BUILD = "Build";
    public static final String STATUS = "Status";
    public static final long minRefreshInterval;

    public static String getBuildUrl(String jobId) {
        return MessageFormat.format(buildURL, jobId);
    }

    public static String getBuildWithParametersUrl(String jobId, Map<String, String> parameters) throws UnsupportedEncodingException {
        Set<String> keySet = parameters.keySet();
        String parametersString = "";
        char delimiter = '?';
        for (String key : keySet) {
            parametersString += (delimiter + key + "=" + parameters.get(key));
            delimiter = '&';
        }
        return MessageFormat.format(buildWithParametersURL, jobId, parametersString);
    }

    public static String[] getStatusUrls(String jobId) {
        return new String[]{statusWebURL, MessageFormat.format(statusURL, jobId)};
    }

}
