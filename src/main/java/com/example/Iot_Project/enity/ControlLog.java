package com.example.Iot_Project.enity;

import com.example.Iot_Project.enums.CommandStatus;
import com.example.Iot_Project.enums.IssueBy;
import com.example.Iot_Project.enums.ModeControl;
import com.example.Iot_Project.enums.Reason;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "control_logs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControlLog {
    @Id
    String id;
    String classroomId;
    Instant timestamp;
    CurrentState command;
    Reason reason;
    ModeControl mode;
    CommandStatus status;
}
