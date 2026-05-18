package com.example.Iot_Project.model;

import com.example.Iot_Project.enums.ConnectivityStatus;
import com.example.Iot_Project.enums.DeviceType;
import com.example.Iot_Project.enums.PowerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Device {
    String deviceId;
    DeviceType type;
    ConnectivityStatus connectivity;
    PowerStatus power;
    Instant lastSeen;
}
