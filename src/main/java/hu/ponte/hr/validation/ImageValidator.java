package hu.ponte.hr.validation;

import hu.ponte.hr.exception.exceptions.MultipartFileException;
import hu.ponte.hr.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Custom validator class for validating Multipart files.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageValidator {

    /**
     * Max allowed size of files to be uploaded.
     */
    @Value("${max-size}")
    private Long maxSize;

    /**
     * Allowed types.
     */
    @Value("${allowed-types}")
    private String[] allowedTypes;

    /**
     * {@link ImageRepository} instance.
     */
    private final ImageRepository imageRepository;

    /**
     * Validates a Multipart file based on predefined ruleset.
     * Throws {@link MultipartFileException} if any of these prevail:
     * file is null or too large, the filename is already used or the type is not allowed.
     *
     * @param file the Multipart file to be uploaded
     */
    public void validate(MultipartFile file) {
        if (file == null) {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            log.error("{} method was called with null parameter(s).", methodName);
            throw new IllegalArgumentException();
        }
        if (file.getSize() > maxSize) {
            String msg = String.format("Image (name: %s) is too large (size: %s).", file.getOriginalFilename(), file.getSize());
            log.error(msg);
            throw new MultipartFileException(msg);
        }
        String originalFilename = file.getOriginalFilename();
        if (imageRepository.findByName(originalFilename).isPresent()) {
            String msg = String.format("Image name (%s) is already used.", file.getOriginalFilename());
            log.error(msg);
            throw new MultipartFileException(msg);
        }
        Tika tika = new Tika();
        try {
            String mimeType = tika.detect(file.getInputStream());
            boolean isValid = false;
            for (String allowedType : allowedTypes) {
                if (mimeType.equals(allowedType)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                String msg = String.format("Image (name: %s) type (%s) is not allowed.", file.getOriginalFilename(), mimeType);
                log.error(msg);
                throw new MultipartFileException(msg);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Sets allowed types externally. It provides testing functionality.
     *
     * @param allowedTypes string array which contains the string representation of allowed types
     */
    protected void setAllowedTypes(String[] allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    /**
     * Sets maxSize externally. It provides testing functionality.
     *
     * @param maxSize max allowed size
     */
    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }
}
