package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.SensorReadingRequest;
import com.example.Iot_Project.dto.response.SensorReadingResponse;
import com.example.Iot_Project.enity.SensorReading;
import com.example.Iot_Project.mapper.SensorReadingMapper;
import com.example.Iot_Project.repository.SensorReadingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SensorReadingService {
    SensorReadingRepository sensorReadingRepository;
    SensorReadingMapper sensorReadingMapper;

    public List<SensorReadingResponse> getListBetweenTimeStamp(Instant start, Instant end, String id){
        return sensorReadingMapper.toSensorReadingResponses(sensorReadingRepository.findByClassroomIdAndTimestampBetween(id, start, end));
    }

    public List<SensorReadingResponse> get20ByClassroomIdOrderByTimeStamp(String id){
        return sensorReadingMapper.toSensorReadingResponses(sensorReadingRepository.findTop20ByClassroomIdOrderByTimestampDesc(id));
    }
    public SensorReadingResponse save(SensorReadingRequest sensorReadingRequest){
        SensorReading sensorReading = sensorReadingMapper.toSensorReading(sensorReadingRequest);
        return sensorReadingMapper.toSensorReadingResponse(sensorReadingRepository.save(sensorReading));
    }
    public SensorReadingResponse getByClassroomIdLatestOrderByTimestampDesc(String classroomId){
        SensorReading sensorReading = sensorReadingRepository.findFirstByClassroomIdOrderByTimestampDesc(classroomId);
        return sensorReadingMapper.toSensorReadingResponse(sensorReading);
    }
}
