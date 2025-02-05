package itda.ieoso.Announcement;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequestDto {
    private String title;
    private String content;
}
