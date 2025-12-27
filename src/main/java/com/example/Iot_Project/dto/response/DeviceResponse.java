package com.example.Iot_Project.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceResponse {
    String id;
    String name;
    String type;
    String ownerId;
    List<String> authorizedUserIds;
    String roomId;
    String status; // ACTIVE / INACTIVE
    Instant lastCommunication;
    Instant eventTime;
}
