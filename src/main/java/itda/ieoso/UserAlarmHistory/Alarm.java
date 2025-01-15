package itda.ieoso.UserAlarmHistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Alarm {
    @Id
    @Column(name = "alarm_id", nullable = false)
    private Long alarmId;

    @Column
    private String alarmType;
}
