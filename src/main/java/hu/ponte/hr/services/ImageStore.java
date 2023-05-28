package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.domain.Image;
import hu.ponte.hr.exception.exceptions.MultipartFileException;
import hu.ponte.hr.exception.exceptions.StorageException;
import hu.ponte.hr.mapper.ImageMapper;
import hu.ponte.hr.repository.ImageRepository;
import hu.ponte.hr.services.storagehandler.StorageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Main service class for digital signing, uploading and metadata storing of images.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageStore {

    /**
     * {@link StorageHandler} instance.
     */
    private final StorageHandler storageHandler;

    /**
     * {@link ImageRepository} instance.
     */
    private final ImageRepository imageRepository;

    /**
     * {@link ImageMapper} instance.
     */
    private final ImageMapper imageMapper;

    /**
     * {@link SignService} instance.
     */
    private final SignService signService;

    /**
     * Sign, upload and store metadata in this order of execution. By following this order,
     * it is ensured that any inconsistent data will not be stored anywhere.
     * Throws {@link IllegalArgumentException} if any of the parameters is null.
     * Throws {@link StorageException} if {@link StorageHandler} throws exception.
     *
     * @param file the file to be processed
     */
    public void upload(MultipartFile file) {
        if (file == null) {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            log.error("{} method was called with null parameter(s).", methodName);
            throw new IllegalArgumentException();
        }
        byte[] bytes = readBytes(file);
        try {
            String signature = signService.createSignature(bytes, file.getOriginalFilename());
            storageHandler.uploadAFile(bytes, file.getOriginalFilename()).get();
            imageRepository.save(createImage(file, signature));
            log.info("Image (name: {}) is signed and uploaded; metadata saved in database.", file.getOriginalFilename());
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof StorageException) {
                throw new StorageException(e.getCause());
            }
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Read the content of a MultipartFile instance.
     * Throws {@link MultipartFileException}, if any of the parameters is null.
     *
     * @param file file to be read
     * @return byte array instance with the content of {@code file}
     */
    private byte[] readBytes(MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            String msg = "Failed to read binary content from multipart file.";
            log.error(msg, ex);
            throw new MultipartFileException(msg);
        }
        return bytes;
    }

    /**
     * Create an Image instance for saving metadata in database.
     *
     * @param file      to be saved
     * @param signature to be bound to image
     * @return the corresponding Image instance
     */
    private Image createImage(MultipartFile file, String signature) {
        Image image = new Image();
        image.setPublicId(String.valueOf(RandomUtils.nextInt(1, Integer.MAX_VALUE)));
        image.setName(file.getOriginalFilename());
        image.setMimeType(file.getContentType());
        image.setSize(file.getSize());
        image.setDigitalSign(signature);
        return image;
    }

    /**
     * Get list of ImageMeta instances stored in database.
     *
     * @return list of ImageMeta instances
     */
    public List<ImageMeta> getAllImageMetaData() {
        return imageMapper.mapImagesToImageMetaData(imageRepository.findAll());
    }

    /**
     * Download an image by public id from the underlying storage.
     * Throws {@link EntityNotFoundException} if image metadata is not found in database.
     * Throws {@link StorageException} if {@link StorageHandler} throws exception.
     *
     * @param publicId the public id of the image
     * @return an InputStream instance with the content of the required image
     */
    public InputStream download(String publicId) {
        Optional<Image> optionalImage = imageRepository.findByPublicId(publicId);
        if (optionalImage.isEmpty()) {
            String msg = String.format("Image not found with id: %s.", publicId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        InputStream imageStream = null;
        try {
            imageStream = storageHandler.downloadAFile(optionalImage.get().getName()).get();
            log.info("Image with id: {} is downloaded.", publicId);
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof StorageException) {
                throw new StorageException(e.getCause());
            }
            Thread.currentThread().interrupt();
        }
        return imageStream;
    }

}
