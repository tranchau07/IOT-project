package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.SensorDataRequest;
import com.example.Iot_Project.dto.response.SensorDataResponse;
import com.example.Iot_Project.enity.SensorData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SensorDataMapper {
    SensorData toSensorData(SensorDataRequest request);

    SensorDataResponse toSensorDataResponse(SensorData sensorData);

    List<SensorDataResponse> toSensorDataResponses(List<SensorData> sensorDatas);

    void updateSensorData(@MappingTarget SensorData sensorData, SensorDataRequest request);
}
