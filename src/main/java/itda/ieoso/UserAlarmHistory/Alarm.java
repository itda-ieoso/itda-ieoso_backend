package itda.ieoso.UserAlarmHistory;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id", nullable = false)
    private Long alarmId;

    @Column
    private String alarmType;
}
