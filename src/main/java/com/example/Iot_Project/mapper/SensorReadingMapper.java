package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.SensorReadingRequest;
import com.example.Iot_Project.dto.response.SensorReadingResponse;
import com.example.Iot_Project.enity.SensorReading;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SensorReadingMapper {
    SensorReading toSensorReading(SensorReadingRequest request);
    SensorReadingResponse toSensorReadingResponse(SensorReading data);
    List<SensorReadingResponse> toSensorReadingResponses(List<SensorReading> data);
    void updateSensorReading(@MappingTarget SensorReading data, SensorReadingRequest request);
}
