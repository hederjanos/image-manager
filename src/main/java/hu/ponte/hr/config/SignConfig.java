package hu.ponte.hr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration bean for digital signing properties.
 */
@Component
@ConfigurationProperties("sign")
@Getter
@Setter
public class SignConfig {

    /**
     * Default path of private key in resources folder.
     */
    private String privateKeyPathInResources;

    /**
     * Default key algorithm.
     */
    private String keyAlgorithm;

    /**
     * Default signature algorithm.
     */
    private String signatureAlgorithm;

}
