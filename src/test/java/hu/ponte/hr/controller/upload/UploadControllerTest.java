package hu.ponte.hr.controller.upload;

import hu.ponte.hr.services.ImageStore;
import hu.ponte.hr.util.TestImageReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageStore imageStore;

    @Test
    void testUpload() throws Exception {
        Map<String, byte[]> images = TestImageReader.readTestImagesFromResources("images");
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                images.values().stream().findAny().get()
        );
        doNothing().when(imageStore).upload(any(MultipartFile.class));
        mockMvc.perform(multipart("/api/file/post").file(mockFile))
                .andExpect(status().isOk());
        verify(imageStore).upload(mockFile);
    }

    @Test
    void testUploadingNotAllowedType() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-picture.jpeg",
                MediaType.TEXT_PLAIN_VALUE,
                "beautiful test image".getBytes());
        mockMvc.perform(multipart("/api/file/post").file(mockFile))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(imageStore);
    }

}
