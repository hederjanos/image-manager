package hu.ponte.hr.controller;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.services.ImageStore;
import hu.ponte.hr.util.TestImageReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageStore imageStore;

    @Test
    void testListImages() throws Exception {
        ImageMeta randomMetaImage = createAnImage();
        when(imageStore.getAllImageMetaData()).thenReturn(List.of(randomMetaImage));
        mockMvc.perform(get("/api/images/meta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[0].id").value(randomMetaImage.getPublicId()))
                .andExpect(jsonPath("$[0].name").value(randomMetaImage.getName()))
                .andExpect(jsonPath("$[0].mimeType").value(randomMetaImage.getMimeType()))
                .andExpect(jsonPath("$[0].size").value(randomMetaImage.getSize()))
                .andExpect(jsonPath("$[0].digitalSign").value(randomMetaImage.getDigitalSign()));
        verify(imageStore, times(1)).getAllImageMetaData();
        verifyNoMoreInteractions(imageStore);
    }

    @Test
    void testGetImage() throws Exception {
        byte[] image = TestImageReader.readTestImagesFromResources("images").values().stream().findAny().get();
        when(imageStore.download(anyString())).thenReturn(image);
        MvcResult mvcResult = mockMvc.perform(get("/api/images/preview/{id}", "1234"))
                .andExpect(status().isOk())
                .andReturn();
        assertArrayEquals(image, mvcResult.getResponse().getContentAsByteArray());
        verify(imageStore, times(1)).download(anyString());
        verifyNoMoreInteractions(imageStore);
    }

    private ImageMeta createAnImage() {
        ImageMeta imageMeta = new ImageMeta();
        imageMeta.setPublicId("1234");
        imageMeta.setSize(9876L);
        imageMeta.setMimeType("image/jpeg");
        imageMeta.setName("cat.jpg");
        imageMeta.setDigitalSign("signature");
        return imageMeta;
    }

}
