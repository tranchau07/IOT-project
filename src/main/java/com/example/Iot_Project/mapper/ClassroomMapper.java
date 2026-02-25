package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.ClassroomRequest;
import com.example.Iot_Project.dto.response.ClassroomResponse;
import com.example.Iot_Project.enity.Classroom;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassroomMapper {
    Classroom toClassroom(ClassroomRequest request);
    ClassroomResponse toClassroomResponse(Classroom device);
    List<ClassroomResponse> toClassroomResponses(List<Classroom> devices);
    void updateClassroom(@MappingTarget Classroom device, ClassroomRequest request);
}
