package com.example.Iot_Project.enity;

import com.example.Iot_Project.enums.CommandStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "commands")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Command {

    @Id
    String id;
    String deviceId;
    String command;
    CommandStatus status;
    Instant createAt;
    Instant requestAt;
    Instant sentAt;
    Instant completeAt;
    Object responsePayload;
}
