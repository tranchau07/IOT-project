package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.ControlLogRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.ControlLogResponse;
import com.example.Iot_Project.service.ControlLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/control-logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ControlLogController {

    ControlLogService controlLogService;

    @GetMapping("/between/{id}")
    public ApiResponse<List<ControlLogResponse>> getListBetweenTimeStamp(
            @PathVariable String id,
            @RequestParam Instant start,
            @RequestParam Instant end,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ){
        return ApiResponse.<List<ControlLogResponse>>builder()
                .result(controlLogService.getListBetweenTimeStampWithPage(start, end, id, page, size))
                .build();
    }

    @PostMapping("/send/control")
    public ApiResponse<?> senControlResponse(@RequestBody ControlLogRequest controlLogRequest) throws JsonProcessingException {
        controlLogService.sendControlLog(controlLogRequest);
        return ApiResponse.builder()
                .build();
    }
}
