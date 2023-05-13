package hu.ponte.hr.services.storagehandler;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import hu.ponte.hr.config.AmazonS3Config;
import hu.ponte.hr.exception.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Concrete class for handling remote AWS S3 storage.
 */
@Service
@Slf4j
@Profile("prod")
public class AmazonS3StorageHandler extends AbstractS3StorageHandler implements StorageHandler {

    /**
     * Initialize {@link AmazonS3} client based on the {@link AmazonS3Config} member.
     */
    @Override
    @PostConstruct
    public void init() {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(
                    amazonS3Config.getAccessKey(),
                    amazonS3Config.getSecretKey());
            ClientConfiguration cc = new ClientConfiguration();
            cc.setConnectionTimeout(1000);
            cc.setSocketTimeout(5000);
            cc.setMaxErrorRetry(5);
            amazonS3 = AmazonS3ClientBuilder.standard()
                    .withRegion(amazonS3Config.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withClientConfiguration(cc)
                    .build();
            if (!amazonS3.doesBucketExistV2(amazonS3Config.getBucket())) {
                amazonS3.createBucket(amazonS3Config.getBucket());
            }
            log.info("AmazonS3Client is initialized with bucket: {}", amazonS3Config.getBucket());
        } catch (SdkClientException ex) {
            String msg = "Something went wrong during initialization of S3 client.";
            log.error(msg, ex);
            throw new StorageException(msg);
        }
    }

    /**
     * Sets a {@link  AmazonS3} instance externally. It provides testing functionality.
     *
     * @param amazonS3 client instance to be set
     */
    protected void setAmazonS3(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

}
