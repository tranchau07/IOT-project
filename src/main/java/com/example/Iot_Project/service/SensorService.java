package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.SensorRequest;
import com.example.Iot_Project.dto.response.DeviceResponse;
import com.example.Iot_Project.dto.response.SensorResponse;
import com.example.Iot_Project.enity.Sensor;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.SensorMapper;
import com.example.Iot_Project.repository.DeviceRepository;
import com.example.Iot_Project.repository.SensorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SensorService {
    SensorRepository sensorRepository;
    SensorMapper sensorMapper;
    DeviceService deviceService;

    public SensorResponse create(SensorRequest request){

        Sensor sensor = sensorMapper.toSensor(request);

        return sensorMapper.toSensorResponse(sensorRepository.save(sensor));
    }
    public List<SensorResponse> getAll(){
        List<Sensor> sensors = sensorRepository.findAll();
        return sensorMapper.toSensorResponses(sensors);
    }
    public SensorResponse getById(String id){
        Sensor sensor = sensorRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        return sensorMapper.toSensorResponse(sensor);
    }

    public SensorResponse update(SensorRequest request,String id){
        Sensor sensor = sensorRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        sensorMapper.updateSensor(sensor, request);

        return sensorMapper.toSensorResponse(sensorRepository.save(sensor));
    }

    public void delete(String id){
        sensorRepository.deleteById(id);
    }

    public List<SensorResponse> getByDeviceId(String deviceId){
        return sensorRepository.findByDeviceId(deviceId);
    }
}
