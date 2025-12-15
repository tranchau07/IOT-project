package com.example.Iot_Project.repository;

import com.example.Iot_Project.enity.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface DeviceRepository extends MongoRepository<Device, String> {
    List<Device> findByRoomId(String roomId);

    // Tìm device theo status
    List<Device> findByStatus(String status);

    // Tìm device không giao tiếp từ lâu (offline)
    // @Query: viết câu truy vấn MongoDB tùy chỉnh
    @Query("{'lastCommunication': {$lt: ?0}}")
    List<Device> findInactiveDevices(Instant threshold);
}
