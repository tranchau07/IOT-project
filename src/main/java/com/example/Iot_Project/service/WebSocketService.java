package com.example.Iot_Project.service;


import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enity.SensorData;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WebSocketService {
    SimpMessagingTemplate simpMessagingTemplate;

    public void broadCastSensorData(SensorData sensorData){
        simpMessagingTemplate.convertAndSend("/topic/sensor-data", sensorData);
    }

    public void broadCastDeviceStatus(Device device){
        simpMessagingTemplate.convertAndSend("/topic/device/status", device);
    }

    public void broadCastAlerts(String message){
        simpMessagingTemplate.convertAndSend("/topic/alerts");
    }

}
