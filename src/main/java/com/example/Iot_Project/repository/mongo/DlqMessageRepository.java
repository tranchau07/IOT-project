package com.example.Iot_Project.repository.mongo;

import com.example.Iot_Project.document.DlqMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DlqMessageRepository extends MongoRepository<DlqMessage, String> {
}
