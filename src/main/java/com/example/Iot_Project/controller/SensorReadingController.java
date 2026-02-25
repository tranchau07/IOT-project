package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.SensorReadingResponse;
import com.example.Iot_Project.service.SensorReadingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-readings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensorReadingController {
    SensorReadingService sensorReadingService;

    @GetMapping("/{id}/sensor")
    public ApiResponse<List<SensorReadingResponse>> getListBetweenTimeStamp(
            @PathVariable("id") String id,
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end
    ) {
        return ApiResponse.<List<SensorReadingResponse>>builder()
                .result(sensorReadingService.getListBetweenTimeStamp(start, end, id))
                .build();
    }

    @GetMapping("/limit-20/{id}")
    ApiResponse<List<SensorReadingResponse>> get20ByClassroomIdOrderByTimeStamp(@PathVariable("id") String id){
        return ApiResponse.<List<SensorReadingResponse>>builder()
                .result(sensorReadingService.get20ByClassroomIdOrderByTimeStamp(id))
                .build();
    }

    @GetMapping("/latest/{id}")
    ApiResponse<SensorReadingResponse> get(@PathVariable("id") String id){
        return ApiResponse.<SensorReadingResponse>builder()
                .result(sensorReadingService.getByClassroomIdLatestOrderByTimestampDesc(id))
                .build();
    }
}
