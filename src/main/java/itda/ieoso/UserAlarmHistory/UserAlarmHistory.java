package itda.ieoso.UserAlarmHistory;


import itda.ieoso.Assignment.Assignment;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;

@Entity
@Getter
public class UserAlarmHistory {
    @Id
    @Column(name = "user_alarm_history_id", nullable = false)
    private Long userAlarmHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", nullable = false)
    private Alarm alarm;

    @Column
    private boolean readStatus;

    @Column
    private String alarmTitle;

    @Column
    private String alarmContent;

    @Column
    private Timestamp sendTime;
}
