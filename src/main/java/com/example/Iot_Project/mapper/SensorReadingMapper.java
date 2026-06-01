package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.SensorReadingRequest;
import com.example.Iot_Project.dto.response.SensorReadingResponse;
import com.example.Iot_Project.document.SensorReading;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorReadingMapper {
    SensorReading toSensorReading(SensorReadingRequest request);
    SensorReadingResponse toSensorReadingResponse(SensorReading data);
    List<SensorReadingResponse> toSensorReadingResponses(List<SensorReading> data);
}
