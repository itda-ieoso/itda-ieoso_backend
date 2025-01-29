package itda.ieoso.Material;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MaterialHistoryRepository extends JpaRepository<MaterialHistory, Long> {
    @Modifying
    @Query("DELETE FROM MaterialHistory m WHERE m.material.materialId = :materialId")
    @Transactional
    void deleteAllByMaterialId(@Param("materialId") Long materialId);

}
