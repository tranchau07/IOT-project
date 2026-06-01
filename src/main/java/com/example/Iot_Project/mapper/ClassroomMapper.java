package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.ClassroomRequest;
import com.example.Iot_Project.dto.response.ClassroomResponse;
import com.example.Iot_Project.document.Classroom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassroomMapper {
    Classroom toClassroom(ClassroomRequest request);

    @Mapping(source = "createdAt", target = "createAt")
    ClassroomResponse toClassroomResponse(Classroom classroom);

    List<ClassroomResponse> toClassroomResponses(List<Classroom> classrooms);
    void updateClassroom(@MappingTarget Classroom classroom, ClassroomRequest request);
}
