package testclientside.service;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
 
public class Service {
    private final String baseUrl = "http://rudy-007.com/rudy/clientSideJavafx/";
    
    public Service() {
    }
    
    public String ServiceGet(String url) {
        String respon = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(baseUrl+url);
            httpGet.setHeader(new BasicHeader("Prama", "no-cache"));
            httpGet.setHeader(new BasicHeader("Cache-Control", "no-cache"));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            respon = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
        }
        return respon;
    }
    
    public void ServicePost(String url, ArrayList<NameValuePair> nameValuePairs){
    	try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(baseUrl+url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse res = httpclient.execute(httppost); 
            HttpEntity entity = res.getEntity();
            entity.getContent();
	}catch(IOException | IllegalStateException e) {
	} 
    }
}
