package hu.ponte.hr.integration;

import hu.ponte.hr.controller.ImageMeta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testUploadAndRetrieving() {
        String fileName = "cat.jpg";
        ClassPathResource resource = new ClassPathResource("images/" + fileName);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", resource);
        ResponseEntity<String> uploadResponse = this.restTemplate.postForEntity("/api/file/post", map, String.class);
        assertEquals(HttpStatus.OK, uploadResponse.getStatusCode());

        ResponseEntity<List<ImageMeta>> downloadMetaResponse = this.restTemplate.exchange(
                "/api/images/meta",
                HttpMethod.GET,
                new HttpEntity<>(null, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                });

        List<ImageMeta> body = downloadMetaResponse.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals(fileName, body.get(0).getName());

        ResponseEntity<Void> downloadPreviewResponse = this.restTemplate.getForEntity("/api/images/preview/" + body.get(0).getPublicId(), Void.class);
        assertEquals(HttpStatus.OK, downloadPreviewResponse.getStatusCode());
    }

    @Test
    public void testDownloadNotExisting() {
        ResponseEntity<Void> response = this.restTemplate.getForEntity("/api/images/preview/123", Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
