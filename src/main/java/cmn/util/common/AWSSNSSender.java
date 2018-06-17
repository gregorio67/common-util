import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class AwsSNSSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsSNSSender.class);

	private static AmazonSNS snsClient = null;
	
	private static Object syncObject = new Object();

	/** 
	 * 
	 *<pre>
	 * 1.Description: Initiate AWS SNS Client 
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 */
	public static void init() throws Exception {
		AWSCredentialsProvider provider = new AWSCredentialsProvider() {

			@Override
			public AWSCredentials getCredentials() {
				BasicAWSCredentials creds = new BasicAWSCredentials("AKIAJSRB4DKS4CEHO5VQ",
						"qotNYnCbWdZCgMCOyCR7utIqJWR4UOU2OitdlXz1");
				return creds;
			}

			@Override
			public void refresh() {				
			}
		};
		snsClient = AmazonSNSClient.builder().withRegion(Regions.EU_WEST_1).withCredentials(provider).build();

	}

	/** 
	 * 
	 *<pre>
	 * 1.Description: Send sns message with phone number
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param snsMap send message map
	 * @return result message map
	 * @throws Exception
	 */
	public static Map<String, Object> sendMessage(Map<String, Object> snsMap) throws Exception {
		LOGGER.info("sns message :: {}", snsMap);

		Map<String, Object> msgMap = new HashMap<String, Object>();

		if (snsMap.get("message") == null || snsMap.get("phoneNumber") == null) {
			msgMap.put("status", "F");
			msgMap.put("message", "Message or phoneNumber is null");
			return msgMap;
		}

		/** If snsClient is null, create snsClient **/
		if (snsClient == null) {
			synchronized(syncObject) {
				init();
			}
		}
		String message = String.valueOf(snsMap.get("message"));
		// String phoneNumber = "+821096463649";
		String phoneNumber = String.valueOf(snsMap.get("phoneNumber"));

		Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();

		PublishResult result = snsClient.publish(new PublishRequest().withMessage(message).withPhoneNumber(phoneNumber)
				.withMessageAttributes(smsAttributes));
		
		System.out.println(result);
		msgMap.put("status", "F");
		msgMap.put("message", "Message or phoneNumber is null");
		msgMap.put("result", result.getMessageId());
		return msgMap;
	}

	public static <T> void main(String args[]) throws Exception {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n테스트 메세지\n");
		map.put("phoneNumber", "+8210xxxxxxxxxx");
		init();
		ExecutorService  service = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			
			Callable<T> task = new Callable<T>() {

				@SuppressWarnings("unchecked")
				@Override
				public T call() throws Exception {
					return (T) AwsSNSSender.sendMessage(map);
				}
				
			};
			System.out.println("return" + service.submit(task));
		}
		service.shutdown();
	}
	

	// private static AWSCredentialsProvider buildCredentialsProvider()
	// {
	// if (PropertyUtil.getString("credentials.file") != null) {
	// return new PropertiesFileCredentialsProvider(
	// PropertyUtil.getString("credentials.file"));
	// }
	// else if (PropertyUtil.getString("credentials.access-key-id") != null) {
	// final BasicAWSCredentials creds = new BasicAWSCredentials(
	// PropertyUtil.getString("credentials.access-key-id"),
	// PropertyUtil.getString("credentials.secret-access-key"));
	// return new AWSCredentialsProvider()
	// {
	// @Override
	// public AWSCredentials getCredentials()
	// {
	// return creds;
	// }
	//
	// @Override
	// public void refresh()
	// { }
	// };
	// }
	// else {
	// return new DefaultAWSCredentialsProviderChain();
	// }
	// }
}
