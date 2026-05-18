package com.example.Iot_Project.repository.mongo;

import com.example.Iot_Project.document.SensorReading;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SensorReadingRepository extends MongoRepository<SensorReading, String> {
    List<SensorReading> findTop20ByClassroomIdOrderByTimestampDesc(String classroomId);

    List<SensorReading> findByClassroomIdAndTimestampBetween(
            String classroomId,
            Instant start,
            Instant end);

    SensorReading findFirstByClassroomIdOrderByTimestampDesc(String classroomId);


    @Aggregation(pipeline = {
            "{ $match: { classroomId: ?0, 'environment.occupancy': { $gt: 0 } } }",
            "{ $sort: { timestamp: -1 } }",
            "{ $limit: 1 }"
    })
    Optional<SensorReading> findLastOccupiedReading(String classroomId);

    @Aggregation(pipeline = {
            "{ $match: { classroomId: ?0, timestamp: { $gte: ?1 } } }",
            "{ $group: { _id: null, avgTemp: { $avg: '$environment.temperature' } } }"
    })
    Double getAverageTemp(String classroomId, Instant fromTime);
}
