package dk.cit.fyp.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;

@Configuration
public class ImageStoreConfig {

	 	@Value("${cloud.aws.credentials.accessKey}")
	    private String accessKey;

	    @Value("${cloud.aws.credentials.secretKey}")
	    private String secretKey;

	    @Value("${cloud.aws.region.static}")
	    private String region;
	    
	    @Value("${cloud.aws.bucket.name}")
	    private String bucketName;	    
	    
	    @Bean
	    public BasicAWSCredentials basicAWSCredentials() {
	        return new BasicAWSCredentials(accessKey, secretKey);
	    }

	    @Primary
	    @Bean
	    public AmazonS3Client amazonS3Client(AWSCredentials awsCredentials) {
			AmazonS3Client s3 = new AmazonS3Client(awsCredentials);
			s3.configureRegion((Regions.EU_WEST_1));			
			try {
				s3.createBucket(bucketName);
			} catch (AmazonS3Exception e) {
				Logger.getLogger(ImageStoreConfig.class).error(e.getErrorMessage());
			}
	        return s3;
	    }
}
