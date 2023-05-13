package hu.ponte.hr.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Domain class for mapping database backed image metadata.
 */
@Entity
@Table(name = "image")
@NoArgsConstructor
@Getter
@Setter
public class Image {

    /**
     * Unique id of image.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Public id of image.
     */
    @Column(name = "public_id")
    private String publicId;

    /**
     * Name of image.
     */
    @Column(name = "name")
    private String name;

    /**
     * Mimetype of image.
     */
    @Column(name = "myme_type")
    private String mimeType;

    /**
     * Size of image in bytes.
     */
    @Column(name = "size")
    private Long size;

    /**
     * Digital signature of image.
     */
    @Column(columnDefinition = "TEXT", name = "digital_sign")
    private String digitalSign;

}
