package hu.ponte.hr.services.storagehandler;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import hu.ponte.hr.config.AmazonS3Config;
import hu.ponte.hr.exception.exceptions.StorageException;
import hu.ponte.hr.services.storagehandler.AmazonS3StorageHandler;
import io.findify.s3mock.S3Mock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.SocketUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AmazonS3StorageHandlerTest {

    private static S3Mock mockS3API;
    private static AmazonS3Config amazonS3Config;
    private static AmazonS3 amazonS3;
    private static AmazonS3StorageHandler amazonS3StorageHandler;

    @BeforeAll
    static void init() {
        amazonS3Config = new AmazonS3Config();
        amazonS3Config.setBucket("test-bucket");
        amazonS3Config.setRegion("test-region");

        int port = SocketUtils.findAvailableTcpPort();
        mockS3API = new S3Mock.Builder().withPort(port).withInMemoryBackend().build();
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

        amazonS3StorageHandler = new AmazonS3StorageHandler();
        amazonS3StorageHandler.setAmazonS3Config(amazonS3Config);
        amazonS3StorageHandler.setAmazonS3(amazonS3);
    }

    @Test
    void testNullCheck() {
        assertThrows(IllegalArgumentException.class, () -> amazonS3StorageHandler.uploadAFile(null, null));
        assertThrows(IllegalArgumentException.class, () -> amazonS3StorageHandler.downloadAFile(null));
    }

    @Test
    void testDownLoadNotExisting() {
        assertThrows(StorageException.class, () -> amazonS3StorageHandler.downloadAFile("testFile"));
    }

    @Test
    void testUploading() {
        String testFile = "testFile";
        amazonS3StorageHandler.uploadAFile(testFile.getBytes(), testFile);
        assertDoesNotThrow(() -> amazonS3.getObject(amazonS3Config.getBucket(), testFile));
    }

    @Test
    @SneakyThrows
    void testDownloadExisting() {
        String testFile = "testFile";
        amazonS3StorageHandler.uploadAFile(testFile.getBytes(), testFile);
        CompletableFuture<byte[]> downloadAFile = amazonS3StorageHandler.downloadAFile(testFile);
        assertEquals(testFile.getBytes().length, downloadAFile.get().length);
    }

    @AfterAll
    static void tearDown() {
        mockS3API.shutdown();
    }

}
