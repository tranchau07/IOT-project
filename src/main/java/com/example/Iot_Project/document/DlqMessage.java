package com.example.Iot_Project.document;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dlq_messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DlqMessage {
    @Id
    String id;
    String topic;
    String payload;
    String errorMessage;
    Instant timestamp;
}
