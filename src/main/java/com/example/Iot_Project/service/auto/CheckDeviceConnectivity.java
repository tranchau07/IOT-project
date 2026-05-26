package com.example.Iot_Project.service.auto;

import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.enums.ConnectivityStatus;
import com.example.Iot_Project.repository.mongo.ClassroomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CheckDeviceConnectivity {
    MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 10000)
    public void publishDeviceStatusPeriodically() {
        Instant threshold = Instant.now().minusSeconds(30);
        
        Query query = new Query(Criteria.where("device.lastSeen").lt(threshold)
                                        .and("device.connectivity").ne(ConnectivityStatus.OFFLINE));
        Update update = new Update().set("device.connectivity", ConnectivityStatus.OFFLINE);
        
        long modifiedCount = mongoTemplate.updateMulti(query, update, Classroom.class).getModifiedCount();
        if (modifiedCount > 0) {
            log.warn("[CRON-HEARTBEAT] Phát hiện {} thiết bị mất kết nối quá 30s. Đã chuyển trạng thái sang OFFLINE.", modifiedCount);
        }
    }
}
