package com.example.Iot_Project.service;

import com.example.Iot_Project.document.DlqMessage;
import com.example.Iot_Project.repository.mongo.DlqMessageRepository;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.document.ControlLog;
import com.example.Iot_Project.document.SensorReading;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.model.Environment;
import com.example.Iot_Project.repository.mongo.ClassroomRepository;
import com.example.Iot_Project.repository.mongo.ControlLogRepository;
import com.example.Iot_Project.repository.mongo.SensorReadingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    ControlLogRepository controlLogRepository;
    MongoTemplate mongoTemplate;
    DlqMessageRepository dlqMessageRepository;

    MessageChannel mqttOutboundChannel;
    ObjectMapper objectMapper;
    SimpMessagingTemplate messagingTemplate; // For pushing realtime updates via WebSockets

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomeMessage(Message<?> message) {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        String payload = message.getPayload().toString();
        Object ack = message.getHeaders().get(IntegrationMessageHeaderAccessor.ACKNOWLEDGMENT_CALLBACK);
        
        try {
            MqttTopic type = MqttTopic.match(topic);
            if (type == null) {
                log.warn("[MQTT-IN] Unknown topic: {}", topic);
            } else {
                processMessageWithRetry(type, topic, payload);
            }
        } catch (Exception e) {
            log.error("[MQTT-IN] Failed to process message, sending to DLQ: {}", e.getMessage());
            saveToDlq(topic, payload, e.getMessage());
        } finally {
            if (ack instanceof AcknowledgmentCallback callback) {
                if (!callback.isAcknowledged()) {
                    callback.acknowledge(AcknowledgmentCallback.Status.ACCEPT);
                }
            }
        }
    }

    private void saveToDlq(String topic, String payload, String errorMsg) {
        try {
            DlqMessage dlq = DlqMessage.builder()
                    .topic(topic)
                    .payload(payload)
                    .errorMessage(errorMsg)
                    .timestamp(Instant.now())
                    .build();
            dlqMessageRepository.save(dlq);
        } catch (Exception e) {
            log.error("Failed to save message to DLQ!", e);
        }
    }

    private void processMessageWithRetry(MqttTopic type, String topic, String payload) throws Exception {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                switch (type) {
                    case SENSOR_READING -> handleSensorReading(topic, payload);
                    case STATE -> handleDeviceState(topic, payload);
                    case RESPONSE -> handleControlResponse(topic, payload);
                }
                return; // Success
            } catch (JsonProcessingException e) {
                throw e; // Do not retry JSON parsing errors
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw e; // Exhausted retries
                }
                log.warn("Database/Processing error, retrying {}/{}...", i + 1, maxRetries);
                Thread.sleep(1000); // Backoff
            }
        }
    }

    private void handleHeartBeat(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("device.lastSeen", Instant.now())
                .set("device.connectivity", ConnectivityStatus.ONLINE);
        mongoTemplate.updateFirst(query, update, Classroom.class);
    }

    private void handleSensorReading(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        Environment environment = objectMapper.convertValue(jsonNode.get("environment"), Environment.class);
        Double voltage = jsonNode.has("voltage") ? jsonNode.get("voltage").asDouble() : null;
        
        // Fix: Added missing sensor fields parsing for Firmware Team Integration
        Boolean smokeDetected = jsonNode.has("smokeDetected") ? jsonNode.get("smokeDetected").asBoolean() : null;
        Boolean doorOpen = jsonNode.has("doorOpen") ? jsonNode.get("doorOpen").asBoolean() : null;

        String[] parts = topic.split("/");
        String classroomId = parts[2];
        String deviceId = parts[3];

        if (!classroomRepository.existsById(classroomId)) {
            log.warn("[MQTT-IN] Bỏ qua Sensor Telemetry do phòng {} không tồn tại trong Database.", classroomId);
            return;
        }

        SensorReading sensorReading = SensorReading.builder()
                .classroomId(classroomId)
                .deviceId(deviceId)
                .environment(environment)
                .timestamp(Instant.now())
                .voltage(voltage)
                .smokeDetected(smokeDetected)
                .doorOpen(doorOpen)
                .build();

        log.info("[MQTT-IN] Nhận Sensor Telemetry từ phòng {}: Temp={}C, Hum={}%, Occ={}, Light={}, Voltage={}V, Smoke={}, Door={}", 
                 classroomId, environment.getTemperature(), environment.getHumidity(), 
                 environment.getOccupancy(), environment.getLightLevel(), voltage, smokeDetected, doorOpen);

        handleHeartBeat(classroomId);
        SensorReading savedReading = sensorReadingRepository.save(sensorReading);
        
        // Realtime Push to Frontend via WebSocket
        log.info("[WEBSOCKET] Đẩy dữ liệu Sensor mới nhất tới kênh /topic/classroom/{}/sensors", classroomId);
        messagingTemplate.convertAndSend("/topic/classroom/" + classroomId + "/sensors", savedReading);
    }

    private void handleDeviceState(String topic, String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        PowerStatus power = PowerStatus.valueOf(jsonNode.get("power").asText());

        String[] parts = topic.split("/");
        String classroomId = parts[2];

        if (!classroomRepository.existsById(classroomId)) {
            log.warn("[MQTT-IN] Bỏ qua Heartbeat State do phòng {} không tồn tại trong Database.", classroomId);
            return;
        }

        Query query = new Query(Criteria.where("_id").is(classroomId));
        Update update = new Update()
                .set("device.lastSeen", Instant.now())
                .set("device.power", power)
                .set("device.connectivity", ConnectivityStatus.ONLINE);
        mongoTemplate.updateFirst(query, update, Classroom.class);

        log.info("[MQTT-IN] Cập nhật State (Heartbeat) từ phòng {}: Power={}, Connectivity=ONLINE", classroomId, power);

        // Realtime push
        Map<String, String> stateUpdate = new HashMap<>();
        stateUpdate.put("power", power.name());
        stateUpdate.put("connectivity", ConnectivityStatus.ONLINE.name());
        messagingTemplate.convertAndSend("/topic/classroom/" + classroomId + "/state", stateUpdate);
        log.info("[WEBSOCKET] Đẩy dữ liệu State tới kênh /topic/classroom/{}/state", classroomId);
    }

    private void handleControlResponse(String topic, String payload) throws JsonProcessingException {

        JsonNode jsonNode = objectMapper.readTree(payload);

        String controlId = jsonNode.get("controlId").asText();
        CommandStatus status = CommandStatus.valueOf(jsonNode.get("status").asText());

        String[] parts = topic.split("/");
        String classroomId = parts[2];

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASSROOM_NOT_EXISTED));

        ControlLog controlLog = controlLogRepository.findById(controlId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTROL_LOG_NOT_EXISTED));

        if (controlLog.getStatus() == CommandStatus.SUCCESS ||
                controlLog.getStatus() == CommandStatus.FAILED) {
            log.info("[MQTT-IN] Lệnh {} cho phòng {} đã được chốt trạng thái {} từ trước, bỏ qua gói tin trùng lặp.", controlId, classroomId, controlLog.getStatus());
            return;
        }

        controlLog.setStatus(status);
        controlLog.setTimestamp(Instant.now());
        controlLog.getCommand().setLastUpdated(Instant.now());

        if (status == CommandStatus.SUCCESS) {
            if (controlLog.getCommand().getPower() != PowerStatus.CLEAR_FAULT) {
                classroom.setCurrentState(controlLog.getCommand());
                classroom.getCurrentState().setLastUpdated(Instant.now());
            } else {
                if (classroom.getCurrentState() != null) {
                    classroom.getCurrentState().setPower(PowerStatus.OFF);
                    classroom.getCurrentState().setLastUpdated(Instant.now());
                }
            }
        }

        classroom.getDevice().setLastSeen(Instant.now());

        controlLogRepository.save(controlLog);
        classroomRepository.save(classroom);

        // Push real-time update
        log.info("[MQTT-IN] Nhận ACK thiết bị từ phòng {}. Lệnh {} trạng thái: {}", classroomId, controlId, status);
        log.info("[WEBSOCKET] Đẩy dữ liệu ControlLog tới kênh /topic/classroom/{}/control", classroomId);
        messagingTemplate.convertAndSend("/topic/classroom/" + classroomId + "/control", controlLog);
    }


    private String buildPayload(String controlId, String classroomId, com.example.Iot_Project.model.CurrentState state) throws JsonProcessingException {

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

        String payload = buildPayload(created.getId(), classroomId, controlLog.getCommand());

        //create message
        Message<?> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();

        log.info("[MQTT-OUT] Đang gửi lệnh điều khiển xuống phòng {} (Device: {}). Lệnh ID: {}. Reason: {}", classroomId, deviceId, created.getId(), controlLog.getReason());
        log.info("[MQTT-OUT] RAW Publish - Topic: {}, Payload: {}", topic, payload);

        boolean sent = mqttOutboundChannel.send(message);

        if (sent) {
            created.setStatus(CommandStatus.SENT);
            created.setTimestamp(Instant.now());
            controlLogRepository.save(created);
            log.info("[MQTT-OUT] Gửi lệnh thành công tới MQTT Broker. Đợi ACK từ thiết bị...");
        } else {
            created.setStatus(CommandStatus.FAILED);
            controlLogRepository.save(created);
            log.error("[MQTT-OUT] LỖI: Không thể gửi lệnh tới MQTT Broker (Command ID: {})", created.getId());
        }
    }
}
