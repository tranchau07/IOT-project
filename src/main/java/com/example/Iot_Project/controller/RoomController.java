package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.RoomRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.RoomResponse;
import com.example.Iot_Project.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

    @PostMapping
    ApiResponse<RoomResponse> create(@RequestBody RoomRequest request){
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoomResponse>> getAll(){
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<RoomResponse> getById(@PathVariable("id") String id){
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<RoomResponse> update(@RequestBody RoomRequest request,@PathVariable("id") String id){
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.update(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<?> delete(@PathVariable String id){
        roomService.delete(id);
        return ApiResponse.builder()
                .result("This room is deleted")
                .build();
    }
}
