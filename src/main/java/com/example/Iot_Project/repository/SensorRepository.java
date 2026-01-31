package com.example.Iot_Project.repository;


import com.example.Iot_Project.dto.response.SensorResponse;
import com.example.Iot_Project.enity.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> {
    // Tìm tất cả sensor của 1 device
    List<SensorResponse> findByDeviceId(String deviceId);

    // Tìm sensor theo type (TEMPERATURE, HUMIDITY...)
    List<Sensor> findByType(String type);
}
