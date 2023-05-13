package hu.ponte.hr.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Metadata class for data transfer of databased backed image meta data.
 */
@Getter
@Setter
@NoArgsConstructor
public class ImageMeta {

    /**
     * Public id of image.
     */
    @JsonProperty("id")
    private String publicId;

    /**
     * Name of image.
     */
    private String name;

    /**
     * Mimetype of image.
     */
    private String mimeType;

    /**
     * Size of image in bytes.
     */
    private long size;

    /**
     * Digital signature of image.
     */
    private String digitalSign;

}
