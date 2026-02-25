package com.example.Iot_Project.dto.response;

import com.example.Iot_Project.enity.Config;
import com.example.Iot_Project.enity.CurrentState;
import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enity.Schedule;
import com.example.Iot_Project.enums.CommandStatus;
import com.example.Iot_Project.enums.IssueBy;
import com.example.Iot_Project.enums.ModeControl;
import com.example.Iot_Project.enums.Reason;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControlLogResponse {
    String id;
    String classroomId;
    Instant timestamp;
    CurrentState command;
    Reason reason;
    ModeControl mode;
    CommandStatus status;
}
