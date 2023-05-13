package hu.ponte.hr.services;

import hu.ponte.hr.domain.Image;
import hu.ponte.hr.exception.exceptions.CustomSignatureException;
import hu.ponte.hr.exception.exceptions.StorageException;
import hu.ponte.hr.mapper.ImageMapper;
import hu.ponte.hr.repository.ImageRepository;
import hu.ponte.hr.services.ImageStore;
import hu.ponte.hr.services.SignService;
import hu.ponte.hr.services.storagehandler.StorageHandler;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageStoreTest {

    @Mock
    private StorageHandler storageHandler;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private SignService signService;

    @InjectMocks
    private ImageStore imageStore;

    @Test
    void testNullCheck() {
        assertThrows(IllegalArgumentException.class, () -> imageStore.upload(null));
        assertThrows(EntityNotFoundException.class, () -> imageStore.download(null));
    }

    @Test
    void testUploading() {
        CompletableFuture<Boolean> uploaded = new CompletableFuture<>();
        uploaded.complete(true);

        when(signService.createSignature(any(byte[].class), anyString())).thenReturn("testSignature");
        when(storageHandler.uploadAFile(any(byte[].class), anyString())).thenReturn(uploaded);
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());

        MockMultipartFile mockFile = new MockMultipartFile("test", "test.jpeg", MediaType.IMAGE_JPEG_VALUE,
                "beautiful test image".getBytes());

        assertDoesNotThrow(() -> imageStore.upload(mockFile));

        verify(signService, times(1)).createSignature(any(byte[].class), anyString());
        verify(storageHandler, times(1)).uploadAFile(any(byte[].class), anyString());
        verify(imageRepository, times(1)).save(any(Image.class));
        verifyNoMoreInteractions(signService, storageHandler, imageRepository);
    }

    @Test
    void testUploadingWhenSigningFail() {
        when(signService.createSignature(any(byte[].class), anyString())).thenThrow(CustomSignatureException.class);

        MockMultipartFile mockFile = new MockMultipartFile("test", "test.jpeg", MediaType.IMAGE_JPEG_VALUE,
                "beautiful test image".getBytes());

        assertThrows(CustomSignatureException.class, () -> imageStore.upload(mockFile));

        verify(signService, times(1)).createSignature(any(byte[].class), anyString());
        verify(storageHandler, times(0)).uploadAFile(any(byte[].class), anyString());
        verify(imageRepository, times(0)).save(any(Image.class));
        verifyNoMoreInteractions(signService, storageHandler, imageRepository);
    }

    @Test
    void testUploadingWhenUploadingFail() {
        when(signService.createSignature(any(byte[].class), anyString())).thenReturn("testSignature");
        when(storageHandler.uploadAFile(any(byte[].class), anyString())).thenThrow(StorageException.class);

        MockMultipartFile mockFile = new MockMultipartFile("test", "test.jpeg", MediaType.IMAGE_JPEG_VALUE,
                "beautiful test image".getBytes());

        assertThrows(StorageException.class, () -> imageStore.upload(mockFile));

        verify(signService, times(1)).createSignature(any(byte[].class), anyString());
        verify(storageHandler, times(1)).uploadAFile(any(byte[].class), anyString());
        verify(imageRepository, times(0)).save(any(Image.class));
        verifyNoMoreInteractions(signService, storageHandler, imageRepository);
    }

    @Test
    void testDownloading() {
        CompletableFuture<byte[]> uploadedImage = new CompletableFuture<>();
        uploadedImage.complete("beautiful test image".getBytes());

        Image image = new Image();
        image.setPublicId(String.valueOf(RandomUtils.nextInt(1, Integer.MAX_VALUE)));
        image.setName("test.jpg");
        image.setMimeType(MediaType.IMAGE_JPEG_VALUE);
        image.setSize(1024L);
        image.setDigitalSign("signature");

        when(imageRepository.findByPublicId(anyString())).thenReturn(Optional.of(image));
        when(storageHandler.downloadAFile(anyString())).thenReturn(uploadedImage);

        assertDoesNotThrow(() -> imageStore.download(image.getPublicId()));

        verify(imageRepository, times(1)).findByPublicId(anyString());
        verify(storageHandler, times(1)).downloadAFile(anyString());
        verifyNoMoreInteractions(imageRepository, storageHandler);
    }

    @Test
    void testDownloadingWhenResourceMotFound() {
        when(imageRepository.findByPublicId(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> imageStore.download("mockFile"));

        verify(imageRepository, times(1)).findByPublicId(anyString());
        verifyNoMoreInteractions(imageRepository);
    }

    @Test
    void testDownloadingWhenResourceMotFoundOnS3() {
        Image image = new Image();
        image.setPublicId(String.valueOf(RandomUtils.nextInt(1, Integer.MAX_VALUE)));
        image.setName("test.jpg");
        image.setMimeType(MediaType.IMAGE_JPEG_VALUE);
        image.setSize(1024L);
        image.setDigitalSign("signature");

        when(imageRepository.findByPublicId(anyString())).thenReturn(Optional.of(image));
        when(storageHandler.downloadAFile(anyString())).thenThrow(StorageException.class);

        assertThrows(StorageException.class, () -> imageStore.download("mockFile"));

        verify(imageRepository, times(1)).findByPublicId(anyString());
        verify(storageHandler, times(1)).downloadAFile(anyString());
        verifyNoMoreInteractions(imageRepository, storageHandler);
    }

}
