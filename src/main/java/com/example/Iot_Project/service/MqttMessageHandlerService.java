package com.example.Iot_Project.service;

import com.example.Iot_Project.enity.SensorData;
import com.example.Iot_Project.enums.MqttTopicType;
import com.example.Iot_Project.enums.Status;
import com.example.Iot_Project.repository.DeviceRepository;
import com.example.Iot_Project.repository.SensorDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MqttMessageHandlerService {
    SensorDataRepository sensorDataRepository;
    WebSocketService webSocketService;
    DeviceRepository deviceRepository;
    MessageChannel mqttOutboundChannel;
    ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomeMessage(Message<?> message) throws JsonProcessingException {
        String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
        String payload = (String) message.getPayload();

        log.info(
                "topic: {}, payload: {}",
                topic,
                payload
        );

        MqttTopicType type = MqttTopicType.match(topic);
        if(type == null) return;

        switch (type) {
            case SENSOR_DATA -> handleSensorData(topic, payload);
            case STATUS -> handleDeviceStatus(topic, payload);
            case HEARTBEAT -> handleHeartbeat(topic);
            case CONTROL_RESPONSE -> handleControlResponse(topic, payload);
        }
    }

    private void handleSensorData(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        Object value = objectMapper.convertValue(jsonNode.get("value"), Object.class);
        SensorData sensorData = SensorData.builder()
                .sensorId(jsonNode.get("sensorId").asText())
                .timestamp(Instant.now())
                .value(value)
                .build();

        log.info("Parsed payload: {}", jsonNode);

        webSocketService.broadCastSensorData(sensorDataRepository.save(sensorData));
    }

    private void handleDeviceStatus(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);

        String id = jsonNode.get("deviceId").asText();
        String status = jsonNode.get("status").asText();

        deviceRepository.findById(id).ifPresent(device -> {
            device.setStatus(status);
            device.setLastCommunication(Instant.now());
            webSocketService.broadCastDeviceStatus(deviceRepository.save(device));
        });

    }
    private void handleHeartbeat(String topic){
        String deviceId = topic.split("/")[1];

        deviceRepository.findById(deviceId).ifPresent(device -> {
            device.setStatus(Status.ACTIVE.name());
            device.setLastCommunication(Instant.now());
            deviceRepository.save(device);
        });
    }
    private void handleControlResponse(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree((payload));

        String commandId = jsonNode.get("commandId").asText();
        String status = jsonNode.get("status").asText();
    }

    public void sendControlCommand(String deviceId, String command){
        //topic
        String topic = String.format("devices/%s/control", deviceId);

        //payload
        String commandId = "CMD_" + System.currentTimeMillis();
        String payload = String.format(
                "{\"commandId\":\"%s\",\"command\":\"%s\",\"timestamp\":\"%s\"}",
                commandId, command, Instant.now()
        );

        //create message
        Message<?> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();

        mqttOutboundChannel.send(message);
    }
}
