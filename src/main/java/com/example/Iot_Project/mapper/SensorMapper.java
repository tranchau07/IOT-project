package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.RoomRequest;
import com.example.Iot_Project.dto.request.SensorRequest;
import com.example.Iot_Project.dto.response.SensorResponse;
import com.example.Iot_Project.enity.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SensorMapper {
    Sensor toSensor(SensorRequest request);

    SensorResponse toSensorResponse(Sensor sensor);

    List<SensorResponse> toSensorResponses(List<Sensor> sensors);

    void updateSensor(@MappingTarget Sensor sensor, SensorRequest request);
}
