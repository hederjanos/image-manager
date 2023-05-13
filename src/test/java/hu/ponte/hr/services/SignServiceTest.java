package hu.ponte.hr.services;

import hu.ponte.hr.config.SignConfig;
import hu.ponte.hr.exception.exceptions.CustomSignatureException;
import hu.ponte.hr.services.SignService;
import hu.ponte.hr.util.TestImageReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SignServiceTest {

    private final Map<String, String> files = new LinkedHashMap<>() {
        {
            put("cat.jpg", "XYZ+wXKNd3Hpnjxy4vIbBQVD7q7i0t0r9tzpmf1KmyZAEUvpfV8AKQlL7us66rvd6eBzFlSaq5HGVZX2DYTxX1C5fJlh3T3QkVn2zKOfPHDWWItdXkrccCHVR5HFrpGuLGk7j7XKORIIM+DwZKqymHYzehRvDpqCGgZ2L1Q6C6wjuV4drdOTHps63XW6RHNsU18wHydqetJT6ovh0a8Zul9yvAyZeE4HW7cPOkFCgll5EZYZz2iH5Sw1NBNhDNwN2KOxrM4BXNUkz9TMeekjqdOyyWvCqVmr5EgssJe7FAwcYEzznZV96LDkiYQdnBTO8jjN25wlnINvPrgx9dN/Xg==");
            put("enhanced-buzz.jpg", "tsLUqkUtzqgeDMuXJMt1iRCgbiVw13FlsBm2LdX2PftvnlWorqxuVcmT0QRKenFMh90kelxXnTuTVOStU8eHRLS3P1qOLH6VYpzCGEJFQ3S2683gCmxq3qc0zr5kZV2VcgKWm+wKeMENyprr8HNZhLPygtmzXeN9u6BpwUO9sKj7ImBvvv/qZ/Tht3hPbm5SrDK4XG7G0LVK9B8zpweXT/lT8pqqpYx4/h7DyE+L5bNHbtkvcu2DojgJ/pNg9OG+vTt/DfK7LFgCjody4SvZhSbLqp98IAaxS9BT6n0Ozjk4rR1l75QP5lbJbpQ9ThAebXQo+Be4QEYV/YXf07WXTQ==");
            put("rnd.jpg", "lM6498PalvcrnZkw4RI+dWceIoDXuczi/3nckACYa8k+KGjYlwQCi1bqA8h7wgtlP3HFY37cA81ST9I0X7ik86jyAqhhc7twnMUzwE/+y8RC9Xsz/caktmdA/8h+MlPNTjejomiqGDjTGvLxN9gu4qnYniZ5t270ZbLD2XZbuTvUAgna8Cz4MvdGTmE3MNIA5iavI1p+1cAN+O10hKwxoVcdZ2M3f7/m9LYlqEJgMnaKyI/X3m9mW0En/ac9fqfGWrxAhbhQDUB0GVEl7WBF/5ODvpYKujHmBAA0ProIlqA3FjLTLJ0LGHXyDgrgDfIG/EDHVUQSdLWsM107Cg6hQg==");
        }
    };

    private Map<String, byte[]> bytesMap;

    private SignConfig signConfig;
    private SignService signService;

    @BeforeEach
    void init() {
        signConfig = new SignConfig();
        signConfig.setPrivateKeyPathInResources("config/keys/key.private");
        signConfig.setKeyAlgorithm("RSA");
        signConfig.setSignatureAlgorithm("SHA256withRSA");
        bytesMap = TestImageReader.readTestImagesFromResources("images");
        signService = new SignService();
        signService.setSignConfig(signConfig);
    }

    @Test
    void testNullCheck() {
        assertThrows(IllegalArgumentException.class, () -> signService.createSignature(null, null));
    }

    @Test
    void testWrongPrivateKeyPath() {
        signConfig.setPrivateKeyPathInResources("");
        bytesMap.forEach((key, value) -> assertThrows(CustomSignatureException.class, () -> signService.createSignature(value, key)));
    }

    @Test
    void testWrongKeyAlgorithm() {
        signConfig.setKeyAlgorithm("");
        bytesMap.forEach((key, value) -> assertThrows(CustomSignatureException.class, () -> signService.createSignature(value, key)));
    }

    @Test
    void testWrongSignatureAlgorithm() {
        signConfig.setPrivateKeyPathInResources("");
        bytesMap.forEach((key, value) -> assertThrows(CustomSignatureException.class, () -> signService.createSignature(value, key)));
    }

    @Test
    void testSigningTestResources() {
        bytesMap.forEach((key, value) -> assertEquals(files.get(key), signService.createSignature(value, key)));
    }


}
