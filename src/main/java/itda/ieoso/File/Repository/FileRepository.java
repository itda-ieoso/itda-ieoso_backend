package itda.ieoso.File.Repository;

import itda.ieoso.File.Domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // Optional method to find a file by its file name
    Optional<File> findByFileName(String fileName);

    // Optional method to find a file by its ID (in case needed)
    Optional<File> findById(Long id);
}