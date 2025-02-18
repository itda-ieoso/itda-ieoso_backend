package itda.ieoso.VideoHistory;

import itda.ieoso.Assignment.AssignmentDTO;
import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureDTO;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoHistoryDto {
    public record Response(
            Long videoId,
            String videoTitle,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long videoHistoryId,
            VideoHistoryStatus videoHistoryStatus,
            Long lectureId,
            Integer orderIndex
    ) {
        public static VideoHistoryDto.Response of(Long videoId, String videoTitle, VideoHistory history, Integer order) {
            return new VideoHistoryDto.Response(
                    videoId,
                    videoTitle,
                    history.getVideo().getStartDate(),
                    history.getVideo().getEndDate(),
                    history.getVideoHistoryId(),
                    history.getVideoHistoryStatus(),
                    history.getVideo().getLecture().getLectureId(),
                    order
            );
        }
    }


}