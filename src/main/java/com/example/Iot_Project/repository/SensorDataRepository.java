package com.example.Iot_Project.repository;


import com.example.Iot_Project.enity.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SensorDataRepository extends MongoRepository<SensorData, String> {
    // Tìm data của 1 sensor, sắp xếp theo thời gian giảm dần
    List<SensorData> findBySensorIdOrderByTimestampDesc(String sensorId);

    // Tìm data trong khoảng thời gian
    @Query("{'sensorId': ?0, 'timestamp': {$gte: ?1, $lte: ?2}}")
    List<SensorData> findBySensorIdAndTimestampBetween(
            String sensorId, Instant from, Instant to);

    // Xóa data cũ (để tiết kiệm dung lượng)
    @Query(value = "{'timestamp': {$lt: ?0}}", delete = true)
    void deleteOldData(Instant threshold);
}
