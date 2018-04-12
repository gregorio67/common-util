import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stis.framework.exception.BizException;
import stis.framework.util.NullUtil;

public class HttpFileUpload {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileUpload.class);
	
	public static String uploadFile(String url, String fileName, Map<String, Object> params) throws Exception {
		if (!NullUtil.isNull(fileName)) {
			return uploadFile(url, new File(fileName), params);
		}
		
		return null;
	}
	
	public static String uploadFile(String url, File uploadFile, Map<String, Object> params) throws Exception {
        
		CloseableHttpClient httpclient = HttpClients
										.custom()
										.setRetryHandler(new CustomRetryHandler())
										.build();
		
	    try {


            List<BasicNameValuePair> reqParam = new LinkedList<BasicNameValuePair>();

            /** build parameter upload request  **/
	    	Iterator<Entry<String, Object>> itr = params.entrySet().iterator();
	    	while(itr.hasNext()) {
	    		String key = itr.next().getKey();
	    		reqParam.add(new BasicNameValuePair(key, params.get(key) != null ? String.valueOf(params.get(key)) : ""));
	    	}
            
            /** Build multipart upload request  **/
            HttpEntity data = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("upfile", uploadFile, ContentType.DEFAULT_BINARY, uploadFile.getName())
                    .build();

            /** Request Configuration **/
            RequestConfig reqConfig = new HttpRequestConfig().init();
            /** build http request and assign multipart upload data **/
            /** Is it possible to set entity twice **/
            HttpUriRequest request = RequestBuilder
                    .post(url)
                    .setEntity(data)
                    .setEntity(new UrlEncodedFormEntity(reqParam,"UTF-8"))
                    .setConfig(reqConfig)
                    .build();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Executing request " + request.getRequestLine());
            }

            /** Create a custom response handler  **/
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
	                int status = response.getStatusLine().getStatusCode();
	                if (status >= 200 && status < 300) {
	                    HttpEntity entity = response.getEntity();
	                    return entity != null ? EntityUtils.toString(entity) : null;
	                } else {
	                    throw new ClientProtocolException("Unexpected response status: " + status);
	                }
				}
            };
            
            /** Call http with parameter **/
            String responseBody = httpclient.execute(request, responseHandler);
            
            return responseBody;
	    }
	    catch(Exception ex) {
	    	LOGGER.error(ex.getMessage());
	    	throw new BizException(ex.getMessage());
	    }
	    finally {
	    	if (httpclient != null) httpclient.close();
	    }
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", "kkimdoy");
		params.put("age", 10);
		uploadFile("http://localhost:8080/sample/fileupload.do", "D:/temp/dymn-websocket.zip", params);
	}
}
