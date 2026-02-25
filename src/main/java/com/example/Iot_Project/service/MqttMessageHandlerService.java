package com.example.Iot_Project.service;

import com.example.Iot_Project.enity.*;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.ControlLogMapper;
import com.example.Iot_Project.repository.ClassroomRepository;
import com.example.Iot_Project.repository.ControlLogRepository;
import com.example.Iot_Project.repository.SensorReadingRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MqttMessageHandlerService {
    ClassroomRepository classroomRepository;
    SensorReadingRepository sensorReadingRepository;
    ControlLogMapper controlLogMapper;
    ControlLogRepository controlLogRepository;

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
        MqttTopic type = MqttTopic.match(topic);
        if(type == null) return;

        switch (type) {
            case SENSOR_READING -> handleSensorReading(topic, payload);
            case STATE -> handleDeviceState(topic, payload);
            case RESPONSE -> handleControlResponse(topic, payload);
        }
    }

    private void handleHeartBeat(String id){
        Classroom classroom = classroomRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));
        Device device = classroom.getDevice();
        device.setLastSeen(Instant.now());

        if(device.getConnectivity() == ConnectivityStatus.OFFLINE){
            device.setConnectivity(ConnectivityStatus.ONLINE);
            classroom.setDevice(device);
        }

        classroomRepository.save(classroom);
    }

    private void handleSensorReading(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        Environment environment = objectMapper.convertValue(jsonNode.get("environment"), Environment.class);
        Double voltage = jsonNode.get("voltage").asDouble();

        String[] parts = topic.split("/");
        String classroomId = parts[2];
        String deviceId = parts[3];

        SensorReading sensorReading = SensorReading.builder()
                .classroomId(classroomId)
                .deviceId(deviceId)
                .environment(environment)
                .timestamp(Instant.now())
                .voltage(voltage)
                .build();

        log.info("Parsed payload: {}", jsonNode);
        handleHeartBeat(classroomId);
        sensorReadingRepository.save(sensorReading);
    }

    private void handleDeviceState(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        PowerStatus power = PowerStatus.valueOf(jsonNode.get("power").asText());

        String[] parts = topic.split("/");
        String deviceId = parts[3];
        String classroomId = parts[2];

        classroomRepository.findById(classroomId).ifPresent(classroom -> {
            classroom.getDevice().setLastSeen(Instant.now());
            classroom.getDevice().setPower(power);
            classroomRepository.save(classroom);
        });
        handleHeartBeat(classroomId);
    }

    private void handleControlResponse(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree((payload));
        String controlId = jsonNode.get("controlId").asText();
        CommandStatus status = CommandStatus.valueOf(jsonNode.get("status").asText());

        String[] parts = topic.split("/");
        String deviceId = parts[3];
        String classroomId = parts[2];

        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() ->
                new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));

        classroom.getDevice().setLastSeen(Instant.now());
        classroom.getCurrentState().setLastUpdated(Instant.now());

       ControlLog controlLog = controlLogRepository.findById(controlId).orElseThrow(() ->
               new AppException(ErrorCode.CONTROL_LOG_NOT_EXISTED));

       controlLog.getCommand().setLastUpdated(Instant.now());
       controlLog.setStatus(status);

        if (controlLog.getStatus() == CommandStatus.SUCCESS
                || controlLog.getStatus() == CommandStatus.FAILED) {
            return;
        }

        controlLog.setStatus(status);
        controlLog.setTimestamp(Instant.now());

        if (status == CommandStatus.SUCCESS) {
            classroom.setCurrentState(controlLog.getCommand());
            classroom.getCurrentState().setLastUpdated(Instant.now());
        }

        controlLogRepository.save(controlLog);
        classroomRepository.save(classroom);

        log.info("Control {} updated to {}", controlId, status);

    }


    private String buildPayload(String controlId,String classroomId, CurrentState state) throws JsonProcessingException {

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("controlId", controlId);
        payloadMap.put("classroomId", classroomId);

        Map<String, Object> commandMap = new HashMap<>();
        commandMap.put("acMode", state.getAcMode().name());
        commandMap.put("acTemp", state.getAcTemp());
        commandMap.put("lightStates", state.getLightStates());
        commandMap.put("fanSpeed", state.getFanSpeed());
        commandMap.put("power", state.getPower().name());

        payloadMap.put("command", commandMap);

        return objectMapper.writeValueAsString(payloadMap);
    }


    public void sendControlCommand(String deviceId, String classroomId, String buildingName, ControlLog controlLog) throws JsonProcessingException {

        String topic = String.format("hust/%s/%s/%s/down/control", buildingName, classroomId, deviceId);

        ControlLog created = controlLogRepository.save(controlLog);
        //payload

        String payload = buildPayload(created.getId() ,classroomId, controlLog.getCommand());

        //create message
        Message<?> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();

        log.info(message.toString());

        boolean sent = mqttOutboundChannel.send(message);

        if (sent) {
            created.setStatus(CommandStatus.SENT);
            created.setTimestamp(Instant.now());
            controlLogRepository.save(created);
        } else {
            created.setStatus(CommandStatus.FAILED);
            controlLogRepository.save(created);
            log.error("MQTT send failed for command {}", created.getId());
        }
    }
}
