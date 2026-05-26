package com.example.Iot_Project.dto.request;

import com.example.Iot_Project.model.Config;
import com.example.Iot_Project.model.CurrentState;
import com.example.Iot_Project.model.Device;
import com.example.Iot_Project.model.Schedule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassroomRequest {
    String id;
    String name;
    String building;
    Integer capacity;
    Device device;
    List<Schedule> schedules;
    Config config;
    CurrentState currentState;
}
