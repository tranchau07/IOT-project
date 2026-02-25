package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.ClassroomRequest;
import com.example.Iot_Project.dto.response.ClassroomResponse;
import com.example.Iot_Project.enity.Classroom;
import com.example.Iot_Project.enity.CurrentState;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.ClassroomMapper;
import com.example.Iot_Project.repository.ClassroomRepository;
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


}
