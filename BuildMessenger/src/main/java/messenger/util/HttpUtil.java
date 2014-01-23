package messenger.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

public class HttpUtil {

    public static String getResponse(String[] urls, String jobName) throws IOException {
        String status = "";
        for (int i = 0; i < urls.length; i++) {
            try {
                String url = urls[i];

                if (status.equals("") && url.contains("api/xml")) {
                    HttpClient client = HttpClientBuilder.create().build();
                    System.out.println(url);
                    HttpUriRequest request = new HttpGet(url);
                    HttpResponse httpResponse = client.execute(request);
                    status = EntityUtils.toString(httpResponse.getEntity());

                }
                else
                {
                    System.out.println(url);
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpUriRequest request = new HttpGet(url);
                    HttpResponse httpResponse = client.execute(request);
                    XPathFactory factory = XPathFactory.newInstance();
                    XPath xPath = factory.newXPath();
                    InputSource inputSource = new InputSource(new ByteArrayInputStream(EntityUtils.toString(httpResponse.getEntity()).getBytes()));
                    String evaluate = "";
                    try {
                        evaluate = xPath.evaluate("/hudson/job[name='" + jobName + "']/color", inputSource);
                        return evaluate;
                    } catch (XPathExpressionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.getMessage();
            }
        }
        return status;
    }
}
