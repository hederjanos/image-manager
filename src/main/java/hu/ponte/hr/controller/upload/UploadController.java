package hu.ponte.hr.controller.upload;

import hu.ponte.hr.services.ImageStore;
import hu.ponte.hr.validation.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dedicated controller layer for uploading an image.
 */
@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
public class UploadController {

    /**
     * {@link ImageStore} instance.
     */
    private final ImageStore imageStore;

    /**
     * {@link ImageValidator} instance.
     */
    private final ImageValidator imageValidator;

    /**
     * Endpoint for uploading an image.
     *
     * @param file the MultipartFile object which represents the image
     * @return OK, if the uploading process (remote storage, digital signing and database backed metadata binding) was successful
     */
    @PostMapping("/post")
    public ResponseEntity<Void> handleFormUpload(@RequestParam("file") MultipartFile file) {
        imageValidator.validate(file);
        imageStore.upload(file);
        return ResponseEntity.ok().build();
    }

}
