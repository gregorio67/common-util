

package ncd.spring.common.sns;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;

public class AwsS3Manager {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Manager.class);

	private static AmazonS3 s3Client = null;

	private static Object syncObject = new Object();

	public static void init() throws Exception {
		AWSCredentialsProvider provider = new AWSCredentialsProvider() {

			@Override
			public AWSCredentials getCredentials() {
				BasicAWSCredentials creds = new BasicAWSCredentials("clientId",
						"clientKey");
				return creds;
			}

			@Override
			public void refresh() {
			}
		};

		s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(provider)
				.build();
		
		LOGGER.info("AWS S3 Client is initiated");

	}

	public static Map<String, Object> upload(String bucketName, String keyName, String fileName) throws Exception {
		return upload(bucketName, keyName, fileName, "SEHATI-ARCHIVE", "application/octet-stream");
	}

	public static Map<String, Object> upload(String bucketName, String keyName, String fileName, String metaTitle) throws Exception {
		return upload(bucketName, keyName, fileName, metaTitle, "application/octet-stream");
	}
	
	public static Map<String, Object> upload(String bucketName, String keyName, String fileName, String metaTitle, String contentType) throws Exception {

		Map<String, Object> msgMap = new HashMap<String, Object>();
		try {
			PutObjectRequest request = new PutObjectRequest(bucketName, keyName, new File(fileName));
			ObjectMetadata metadata = new ObjectMetadata();

			metadata.setContentType(contentType);
			metadata.addUserMetadata("x-amz-meta-title", metaTitle);
			request.setMetadata(metadata);
			
			checkS3Client();
			
			s3Client.putObject(request);			
		}
		catch(Exception ex) {
			msgMap.put("status", "F");
			msgMap.put("message", ex.getMessage());
			return msgMap;
		}

		msgMap.put("status", "S");
		msgMap.put("message", "Successfully uploaded");
		return msgMap;
	}

	public static InputStream download(String bucketName, String keyName) throws Exception {

		
		S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName, keyName));
		return fullObject.getObjectContent();
	
	}
	
	public static InputStream fileDownload(String bucketName, String keyName, String fileName) throws Exception {
		
		ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides().withCacheControl("No-cache")
				.withContentDisposition("attachment; filename" + fileName);
		
		GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, keyName)
				.withResponseHeaders(headerOverrides);
		
		checkS3Client();

		S3Object headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);

		return headerOverrideObject.getObjectContent();
	}
	
	private static void checkS3Client() throws Exception {
		/** Check s3Client is null **/
		if (s3Client == null) {
			synchronized(syncObject) {
				init();
			}
		}		
	}

}
