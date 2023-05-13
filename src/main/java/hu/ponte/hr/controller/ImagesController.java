package hu.ponte.hr.controller;

import hu.ponte.hr.services.ImageStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Digits;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Dedicated controller layer for retrieving images.
 */
@RestController
@RequestMapping("api/images")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ImagesController {

    /**
     * {@link ImageStore} instance for handling uploading, digital signing and metadata storing.
     */
    private final ImageStore imageStore;

    /**
     * Endpoint for listing the metadata for all stored images.
     *
     * @return the list of ImageMeta objects for all stored images
     */
    @GetMapping("meta")
    public ResponseEntity<List<ImageMeta>> listImages() {
        return ResponseEntity.ok(imageStore.getAllImageMetaData());
    }

    /**
     * Endpoint for getting a preview for an image by public id.
     *
     * @param id       the public id of the image
     * @param response the HttpServletResponse object in which the preview will be written
     */
    @GetMapping("preview/{id}")
    public ResponseEntity<Void> getImage(@PathVariable("id") @Digits(integer = Integer.MAX_VALUE, fraction = 0) String id, HttpServletResponse response) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        byte[] image = imageStore.download(id);
        if (image != null) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())) {
                bufferedOutputStream.write(image, 0, image.length);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return ResponseEntity.ok().build();
    }

}
