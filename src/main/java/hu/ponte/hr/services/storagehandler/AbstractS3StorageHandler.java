package hu.ponte.hr.services.storagehandler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import hu.ponte.hr.config.AmazonS3Config;
import hu.ponte.hr.exception.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract class for handling remote AWS S3 storage.
 */
@Slf4j
@Service
public abstract class AbstractS3StorageHandler implements StorageHandler {

    /**
     * {@link AmazonS3Config} instance.
     */
    protected AmazonS3Config amazonS3Config;

    /**
     * {@link AmazonS3} client.
     */
    protected AmazonS3 amazonS3;

    /**
     * Upload data to S3 storage. The uploading process will be executed on a separated thread.
     * If during this call an exception wasn't thrown, data was stored.
     * Throws {@link IllegalArgumentException} if any of the parameters is null.
     * Throws {@link StorageException} if {@link AmazonS3} client throws exception.
     *
     * @param data     data to be uploaded
     * @param fileName to be bind to {@code data}
     * @return a {@link Boolean} instance wrapped in {@link CompletableFuture} object.
     */
    @Override
    @Async
    public CompletableFuture<Boolean> uploadAFile(byte[] data, String fileName) {
        if (data == null || fileName == null) {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            log.error("{} method was called with null parameter(s).", methodName);
            throw new IllegalArgumentException();
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    amazonS3Config.getBucket(),
                    fileName,
                    byteArrayInputStream,
                    metadata);
            amazonS3.putObject(putObjectRequest);
            log.info("File (name: {}, size: {}) is uploaded to S3.", fileName, data.length);
            return CompletableFuture.completedFuture(true);
        } catch (AmazonS3Exception | IOException ex) {
            String msg = String.format("Could not upload file (name: %s, size: %s) to S3.", fileName, data.length);
            log.error(msg, ex);
            throw new StorageException(msg);
        }
    }

    /**
     * Download data from S3 storage by name. The downloading process will be executed on a separated thread.
     * If during this call an exception wasn't thrown, data was retrieved.
     * Throws {@link IllegalArgumentException} if any of the parameters is null.
     * Throws {@link StorageException} if {@link AmazonS3} client throws exception.
     *
     * @param fileName the name of the file to be found
     * @return a byte array instance wrapped in {@link CompletableFuture} object.
     */
    @Override
    @Async
    public CompletableFuture<InputStream> downloadAFile(String fileName) {
        if (fileName == null) {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            log.error("{} method was called with null parameter(s).", methodName);
            throw new IllegalArgumentException();
        }
        try {
            S3Object s3Object = amazonS3.getObject(amazonS3Config.getBucket(), fileName);
            log.info("File (name: {}) is downloaded from S3.", fileName);
            return CompletableFuture.completedFuture(s3Object.getObjectContent());
        } catch (SdkClientException ex) {
            String msg = String.format("Could not found file: %s in bucket: %s.", fileName, amazonS3Config.getBucket());
            log.error(msg, ex);
            throw new StorageException(msg);
        }
    }

    /**
     * Binds a {@link  AmazonS3Config} instance externally. It provides testing functionality without loading a full application context.
     *
     * @param amazonS3Config config instance to be bound
     */
    @Autowired
    protected void setAmazonS3Config(AmazonS3Config amazonS3Config) {
        this.amazonS3Config = amazonS3Config;
    }

}
