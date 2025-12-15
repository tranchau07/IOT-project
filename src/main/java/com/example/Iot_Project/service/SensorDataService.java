package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.SensorDataRequest;
import com.example.Iot_Project.dto.response.SensorDataResponse;
import com.example.Iot_Project.enity.SensorData;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.SensorDataMapper;
import com.example.Iot_Project.repository.SensorDataRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SensorDataService {
    SensorDataRepository sensorDataRepository;
    SensorDataMapper sensorDataMapper;

    public SensorDataResponse create(SensorDataRequest request){

        SensorData sensorData = sensorDataMapper.toSensorData(request);

        return sensorDataMapper.toSensorDataResponse(sensorDataRepository.save(sensorData));
    }
    public List<SensorDataResponse> getAll(){
        List<SensorData> sensorDatas = sensorDataRepository.findAll();
        return sensorDataMapper.toSensorDataResponses(sensorDatas);
    }
    public SensorDataResponse getById(String id){
        SensorData sensorData = sensorDataRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        return sensorDataMapper.toSensorDataResponse(sensorData);
    }

    public List<SensorDataResponse> getLatestSensorData(String sensorDataId, int limit){
         return sensorDataMapper.toSensorDataResponses(sensorDataRepository.findBySensorIdOrderByTimestampDesc(sensorDataId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList()));
    }

    public List<SensorDataResponse> getDataByRange(String sensorId, Instant from, Instant to){
        List<SensorData> data = sensorDataRepository.findBySensorIdAndTimestampBetween(sensorId, from, to);
        return sensorDataMapper.toSensorDataResponses(data);
    }

    public SensorDataResponse update(SensorDataRequest request,String id){
        SensorData sensorData = sensorDataRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        sensorDataMapper.updateSensorData(sensorData, request);

        return sensorDataMapper.toSensorDataResponse(sensorDataRepository.save(sensorData));
    }

    public void delete(String id){
        sensorDataRepository.deleteById(id);
    }


}
