package hu.ponte.hr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration bean for AWS S3 properties.
 */
@Component
@ConfigurationProperties("s3")
@Getter
@Setter
public class AmazonS3Config {

    /**
     * Default access key.
     */
    private String accessKey;

    /**
     * Default secret key.
     */
    private String secretKey;

    /**
     * Default region.
     */
    private String region;

    /**
     * Default name of bucket.
     */
    private String bucket;

}
