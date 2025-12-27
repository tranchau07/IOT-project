package com.example.Iot_Project.dto.response;

import com.example.Iot_Project.enums.CommandResponseStatus;
import com.example.Iot_Project.enums.CommandStatus;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandResponse {
    String commandId;
    String status;
    Instant respondedAt;
}
