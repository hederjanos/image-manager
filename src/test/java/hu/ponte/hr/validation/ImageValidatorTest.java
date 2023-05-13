package hu.ponte.hr.validation;

import hu.ponte.hr.domain.Image;
import hu.ponte.hr.exception.exceptions.MultipartFileException;
import hu.ponte.hr.repository.ImageRepository;
import hu.ponte.hr.validation.ImageValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageValidator imageValidator;

    @BeforeEach
    void init() {
        imageValidator.setMaxSize(2 * 1024 * 1024L);
        imageValidator.setAllowedTypes(new String[]{"image/png", "image/jpg", "image/jpeg", "image/gif"});
    }

    @Test
    void testNullCheck() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> imageValidator.validate(null));
    }

    @Test
    void testSize() {
        MockMultipartFile mockFile = new MockMultipartFile("test", "test.jpeg", MediaType.IMAGE_JPEG_VALUE,
                new byte[2048 * 2048]);
        Assertions.assertThrows(MultipartFileException.class, () -> imageValidator.validate(mockFile));
    }

    @Test
    void testAlreadyUsedName() {
        when(imageRepository.findByName(anyString())).thenReturn(Optional.of(new Image()));
        MockMultipartFile mockFile = new MockMultipartFile("test", "test.jpeg", MediaType.IMAGE_JPEG_VALUE,
                "beautiful test image".getBytes());
        Assertions.assertThrows(MultipartFileException.class, () -> imageValidator.validate(mockFile));
        verify(imageRepository, times(1)).findByName(anyString());
        verifyNoMoreInteractions(imageRepository);
    }

    @Test
    void testNotAllowedMimeType() {
        MockMultipartFile mockFile = new MockMultipartFile("test", "test.jpeg", MediaType.TEXT_PLAIN_VALUE,
                "beautiful test image".getBytes());
        Assertions.assertThrows(MultipartFileException.class, () -> imageValidator.validate(mockFile));
    }


}
