package hu.ponte.hr.repository;

import hu.ponte.hr.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository layer for database backed image metadata retrieving.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Retrieve an Image object from database by public id.
     *
     * @param publicId the public id
     * @return the found Image object wrapped in an Optional
     */
    @Query("SELECT i FROM Image i WHERE i.publicId = :publicId")
    Optional<Image> findByPublicId(@Param("publicId") String publicId);

    /**
     * Retrieve an Image object from database by name.
     *
     * @param name the name
     * @return the found Image object wrapped in an Optional
     */
    @Query("SELECT i FROM Image i WHERE i.name = :name")
    Optional<Image> findByName(@Param("name") String name);

}
