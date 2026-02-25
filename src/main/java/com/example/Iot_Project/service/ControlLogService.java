package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.ControlLogRequest;
import com.example.Iot_Project.dto.response.ControlLogResponse;
import com.example.Iot_Project.enity.Classroom;
import com.example.Iot_Project.enity.ControlLog;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.ControlLogMapper;
import com.example.Iot_Project.repository.ClassroomRepository;
import com.example.Iot_Project.repository.ControlLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ControlLogService {
    ControlLogMapper controlLogMapper;
    ControlLogRepository controlLogRepository;
    MqttMessageHandlerService mqttMessageHandlerService;
    ClassroomRepository classroomRepository;

    public List<ControlLogResponse> getListBetweenTimeStamp(Instant start, Instant end, String classroomId){
        return controlLogMapper.toControlLogResponses(controlLogRepository.findByClassroomIdAndTimestampBetween(classroomId, start, end));
    }

    public void sendControlLog(ControlLogRequest request) throws JsonProcessingException {
        Classroom classroom = classroomRepository.findById(request.getClassroomId()).orElseThrow(() ->
                new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));
        ControlLog log = controlLogMapper.toControlLog(request);
        log.setStatus(CommandStatus.CREATE);
        log.setTimestamp(Instant.now());
        log.setMode(ModeControl.MANUAL);
        log.setReason(Reason.MANUAL_OVERRIDE);
         mqttMessageHandlerService.sendControlCommand(classroom.getDevice().getDeviceId(), classroom.getId(), classroom.getBuilding(), log);
    }




}
