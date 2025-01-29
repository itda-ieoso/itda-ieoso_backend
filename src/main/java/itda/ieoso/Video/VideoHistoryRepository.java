package itda.ieoso.Video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VideoHistoryRepository extends JpaRepository<VideoHistory, Long> {
    @Modifying
    @Query("DELETE FROM VideoHistory m WHERE m.video.videoId = :videoId")
    @Transactional
    void deleteAllByVideoId(@Param("videoId") Long videoId);

}
