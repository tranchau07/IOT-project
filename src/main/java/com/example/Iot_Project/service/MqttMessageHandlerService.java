package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.CommandRequest;
import com.example.Iot_Project.dto.response.CommandResponse;
import com.example.Iot_Project.enity.Command;
import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enity.SensorData;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.repository.CommandRepository;
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
    CommandRepository commandRepository;
    DeviceRepository deviceRepository;


    WebSocketService webSocketService;
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
            device.setStatus(DeviceStatus.ACTIVE.name());
            device.setLastCommunication(Instant.now());
            deviceRepository.save(device);
        });
    }

    private void handleControlResponse(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree((payload));
        CommandResponse response =  objectMapper.treeToValue(jsonNode, CommandResponse.class);

        Command command = commandRepository.findById(response.getCommandId()).orElseThrow(()
                -> new AppException(ErrorCode.COMMAND_NOT_EXISTED));

        log.info("1");
        if (command.getStatus() == CommandStatus.SUCCESS
                || command.getStatus() == CommandStatus.FAILED
                || command.getStatus() == CommandStatus.TIMEOUT) {
            return;
        }
        log.info("2");

        CommandStatus newStatus = mapToCommandStatus(CommandResponseStatus.valueOf(response.getStatus()));

        log.info(newStatus.toString());

        command.setStatus(newStatus);
        command.setResponsePayload(response);
        command.setCompleteAt(Instant.now());
        commandRepository.save(command);
    }

    private CommandStatus mapToCommandStatus(CommandResponseStatus responseStatus) {
        return switch (responseStatus) {
            case SUCCESS -> CommandStatus.SUCCESS;
            case ERROR -> CommandStatus.FAILED;
        };
    }

    public void sendControlCommand(String deviceId, CommandRequest request){
        //topic
        String topic = String.format("devices/%s/control", deviceId);
        String commandId = "CMD_" + System.currentTimeMillis();
        Instant now = Instant.now();

        Device device = deviceRepository.findById(deviceId).orElseThrow(()
                -> new AppException(ErrorCode.DEVICE_NOT_EXISTED));

        if(device.getStatus().equals(DeviceStatus.INACTIVE.name()))
            throw new AppException(ErrorCode.DEVICE_OFFLINE);

        Command cmd = Command.builder()
                .id(commandId)
                .command(request.getCommand())
                .deviceId(deviceId)
                .requestAt(request.getRequestAt())
                .createAt(now)
                .status(CommandStatus.CREATED)
                .build();

        commandRepository.save(cmd);
        //payload

        String payload = String.format(
                "{\"commandId\":\"%s\",\"command\":\"%s\",\"serverSentAt\":\"%s\"}",
                commandId, request.getCommand(), now
        );

        //create message
        Message<?> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();

        mqttOutboundChannel.send(message);

        cmd.setSentAt(Instant.now());
        cmd.setStatus(CommandStatus.SENT);
        commandRepository.save(cmd);
    } // chức năng timeOut sẽ để lại cho tới khi hoàn thiện cơ bản các chức năng của dự án

}
