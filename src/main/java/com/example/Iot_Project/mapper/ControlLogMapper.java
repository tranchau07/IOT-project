package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.ControlLogRequest;
import com.example.Iot_Project.dto.response.ControlLogResponse;
import com.example.Iot_Project.enity.ControlLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ControlLogMapper {
    ControlLog toControlLog(ControlLogRequest request);
    ControlLogResponse toControlLogResponse(ControlLog device);
    List<ControlLogResponse> toControlLogResponses(List<ControlLog> devices);
    void updateControlLog(@MappingTarget ControlLog device, ControlLogRequest request);
}
