package com.example.Iot_Project.service;

import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.document.ControlLog;
import com.example.Iot_Project.dto.request.ControlLogRequest;
import com.example.Iot_Project.dto.response.ControlLogResponse;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.ControlLogMapper;
import com.example.Iot_Project.repository.mongo.ClassroomRepository;
import com.example.Iot_Project.repository.mongo.ControlLogRepository;
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

    public List<ControlLogResponse> getListBetweenTimeStampWithPage(Instant start, Instant end, String classroomId, int page, int size){
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, 
                size, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp")
        );
        org.springframework.data.domain.Page<ControlLog> resultPage = controlLogRepository.findByClassroomIdAndTimestampBetween(classroomId, start, end, pageable);
        return controlLogMapper.toControlLogResponses(resultPage.getContent());
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
