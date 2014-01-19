package messenger.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

    public static String getResponse(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
//        System.out.println(url);
        HttpUriRequest request = new HttpGet(url);
        HttpResponse httpResponse = client.execute(request);
        return EntityUtils.toString(httpResponse.getEntity());
    }

}
