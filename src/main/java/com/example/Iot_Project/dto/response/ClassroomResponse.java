package com.example.Iot_Project.dto.response;

import com.example.Iot_Project.enity.Config;
import com.example.Iot_Project.enity.CurrentState;
import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enity.Schedule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassroomResponse {
    String id;
    String name;
    String building;
    Integer capacity;
    Device device;
    List<Schedule> schedules;
    Config config;
    CurrentState currentState;
    Instant createAt;
}
