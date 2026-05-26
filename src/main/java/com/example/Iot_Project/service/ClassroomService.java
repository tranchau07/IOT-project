package com.example.Iot_Project.service;

import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.dto.request.ClassroomRequest;
import com.example.Iot_Project.dto.response.ClassroomResponse;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.ClassroomMapper;
import com.example.Iot_Project.model.CurrentState;
import com.example.Iot_Project.document.ControlLog;
import com.example.Iot_Project.enums.CommandStatus;
import com.example.Iot_Project.enums.ModeControl;
import com.example.Iot_Project.enums.PowerStatus;
import com.example.Iot_Project.enums.Reason;
import com.example.Iot_Project.repository.mongo.ClassroomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassroomService {
    ClassroomRepository classroomRepository;
    ClassroomMapper classroomMapper;
    MqttMessageHandlerService mqttMessageHandlerService;

    public ClassroomResponse create(ClassroomRequest request){
        if(classroomRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CLASSROOM_EXISTED);

        Classroom classroom = classroomMapper.toClassroom(request);
        classroom.setCreatedAt(Instant.now());
        classroom.getCurrentState().setLastUpdated(Instant.now());
        classroom.getDevice().setLastSeen(Instant.now());
        return classroomMapper.toClassroomResponse(classroomRepository.save(classroom));
    }

    public List<ClassroomResponse> getList(){
        return classroomMapper.toClassroomResponses(classroomRepository.findAll());
    }

    public ClassroomResponse getById(String id){
        return classroomMapper.toClassroomResponse(classroomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASSROOM_NOT_EXISTED)));
    }

    public ClassroomResponse update(ClassroomRequest request, String id){
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));
        classroomMapper.updateClassroom(classroom, request);
        return classroomMapper.toClassroomResponse(classroomRepository.save(classroom));
    }

    public void delete(String id){
        classroomRepository.delete(classroomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASSROOM_NOT_EXISTED)));
    }

    public void clearFault(String id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));

        classroom.setFaultLatched(false);
        classroomRepository.save(classroom);

        CurrentState clearState = new CurrentState();
        clearState.setPower(PowerStatus.CLEAR_FAULT);
        clearState.setAcMode(com.example.Iot_Project.enums.AcMode.OFF);
        clearState.setAcTemp(0.0);
        clearState.setLightStates(java.util.Collections.emptyList());
        clearState.setFanSpeed(java.util.Collections.emptyList());

        ControlLog controlLog = new ControlLog();
        controlLog.setCommand(clearState);
        controlLog.setReason(Reason.MANUAL_OVERRIDE);
        controlLog.setMode(ModeControl.MANUAL);
        controlLog.setStatus(CommandStatus.PENDING);
        controlLog.setTimestamp(Instant.now());
        controlLog.setClassroomId(classroom.getId());

        try {
            mqttMessageHandlerService.sendControlCommand(
                    classroom.getDevice().getDeviceId(),
                    classroom.getId(),
                    classroom.getBuilding(),
                    controlLog
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send CLEAR_FAULT command", e);
        }
    }

    public void turnOffDevice(String id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));

        CurrentState current = classroom.getCurrentState();
        CurrentState offState = new CurrentState();

        int lightCount = (current != null && current.getLightStates() != null) ? current.getLightStates().size() : 2;
        int fanCount = (current != null && current.getFanSpeed() != null) ? current.getFanSpeed().size() : 2;

        offState.setPower(PowerStatus.OFF);
        offState.setAcMode(com.example.Iot_Project.enums.AcMode.OFF);
        offState.setAcTemp(0.0);
        offState.setLightStates(java.util.Collections.nCopies(lightCount, 0));
        offState.setFanSpeed(java.util.Collections.nCopies(fanCount, 0));
        offState.setLastUpdated(Instant.now());

        ControlLog controlLog = new ControlLog();
        controlLog.setCommand(offState);
        controlLog.setReason(Reason.MANUAL_OVERRIDE);
        controlLog.setMode(ModeControl.MANUAL);
        controlLog.setStatus(CommandStatus.PENDING);
        controlLog.setTimestamp(Instant.now());
        controlLog.setClassroomId(classroom.getId());

        try {
            mqttMessageHandlerService.sendControlCommand(
                    classroom.getDevice().getDeviceId(),
                    classroom.getId(),
                    classroom.getBuilding(),
                    controlLog
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OFF command", e);
        }
    }
}
