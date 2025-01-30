package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findAllByCourse(Course course);
}
