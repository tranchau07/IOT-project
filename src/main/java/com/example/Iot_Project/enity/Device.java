package com.example.Iot_Project.enity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "devices")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Device {
    @Id
    String id;
    String name;
    String type;
    String roomId;
    String status; // ACTIVE / INACTIVE
    Instant lastCommunication;
}
