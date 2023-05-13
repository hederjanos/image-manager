package hu.ponte.hr.services.storagehandler;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import hu.ponte.hr.config.AmazonS3Config;
import hu.ponte.hr.exception.exceptions.StorageException;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Concrete class for handling remote AWS S3 storage in a test environment locally.
 */
@Service
@Slf4j
@Profile("test")
public class TestAmazonS3StorageHandler extends AbstractS3StorageHandler implements StorageHandler {

    /**
     * Mock S3 API ({@link S3Mock}) for simulating AWS cloud.
     */
    private S3Mock mockS3API;

    /**
     * Initialize {@link S3Mock} private member.
     * Initialize {@link AmazonS3} client based on the {@link AmazonS3Config} member.
     */
    @Override
    @PostConstruct
    public void init() {
        try {
            int port = SocketUtils.findAvailableTcpPort();
            String tempDir = System.getProperty("java.io.tmpdir");
            mockS3API = new S3Mock.Builder().withPort(port).withFileBackend(tempDir).build();
            mockS3API.start();
            AwsClientBuilder.EndpointConfiguration endpoint =
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:" + port, amazonS3Config.getRegion());
            amazonS3 = AmazonS3ClientBuilder
                    .standard()
                    .withPathStyleAccessEnabled(true)
                    .withEndpointConfiguration(endpoint)
                    .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                    .build();
            amazonS3.createBucket(amazonS3Config.getBucket());
            log.info("AmazonS3Client is initialized with bucket: {}", amazonS3Config.getBucket());
        } catch (SdkClientException ex) {
            String msg = "Something went wrong during initialization of S3 client.";
            log.error(msg, ex);
            throw new StorageException(msg);
        }
    }

    /**
     * Shut down mock S3 API.
     */
    @PreDestroy
    public void shutDown() {
        mockS3API.shutdown();
    }

}
