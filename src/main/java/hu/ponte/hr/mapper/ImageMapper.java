package hu.ponte.hr.mapper;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.domain.Image;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for REST.
 */
@Mapper(componentModel = "spring")
public interface ImageMapper {

    /**
     * Convert an {@link  Image} object to {@link ImageMeta} instance.
     *
     * @param image the Image object to be mapped
     * @return an ImageMeta instance
     */
    ImageMeta mapImageToImageMeta(Image image);

    /**
     * Convert list of {@link  Image} objects to list of {@link ImageMeta} instances.
     *
     * @param images list of Images objects to be mapped
     * @return list of ImageMeta instances
     */
    List<ImageMeta> mapImagesToImageMetaData(List<Image> images);

}
