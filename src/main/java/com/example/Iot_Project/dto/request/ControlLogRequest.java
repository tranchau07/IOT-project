package com.example.Iot_Project.dto.request;

import com.example.Iot_Project.enity.CurrentState;
import com.example.Iot_Project.enums.CommandStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControlLogRequest {
    CurrentState command;
    String classroomId;
}
