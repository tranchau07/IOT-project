package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.ClassroomRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.ClassroomResponse;
import com.example.Iot_Project.mapper.ClassroomMapper;
import com.example.Iot_Project.service.ClassroomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassroomController {
    ClassroomService classroomService;
    ClassroomMapper classroomMapper;

    @PostMapping("")
    ApiResponse<ClassroomResponse> create(@RequestBody ClassroomRequest request){
        return ApiResponse.<ClassroomResponse>builder()
                .result(classroomService.create(request))
                .build();
    }

    @GetMapping("")
    ApiResponse<List<ClassroomResponse>> getList(){
        return ApiResponse.<List<ClassroomResponse>>builder()
                .result(classroomService.getList())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ClassroomResponse> getById(@PathVariable("id") String id){
        return ApiResponse.<ClassroomResponse>builder()
                .result(classroomService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ClassroomResponse> update(@RequestBody ClassroomRequest request, @PathVariable("id") String id){
        return ApiResponse.<ClassroomResponse>builder()
                .result(classroomService.update(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<?> delete(@PathVariable String id){
        return ApiResponse.builder().build();
    }
}
