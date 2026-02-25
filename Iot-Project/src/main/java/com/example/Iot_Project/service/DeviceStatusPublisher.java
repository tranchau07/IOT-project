package com.example.Iot_Project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeviceStatusPublisher {
    DeviceRepository deviceRepository;
    MessageChannel mqttOutboundChannel;
    ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "10000")
    public void publishDeviceStatusPeriodically() {

        deviceRepository.findAll().forEach(device -> {
            try {
                ObjectNode payload = objectMapper.createObjectNode();
                payload.put("deviceId", device.getId());
                payload.put("status", device.getStatus());

                String payloadStr = objectMapper.writeValueAsString(payload);
                String topic = String.format("devices/%s/status", device.getId());

                Message<?> message = MessageBuilder
                        .withPayload(payloadStr)
                        .setHeader(MqttHeaders.TOPIC, topic)
                        .setHeader(MqttHeaders.QOS, 1)
                        .build();

                mqttOutboundChannel.send(message);

            } catch (Exception e) {
                log.error("Failed to publish status for device {}", device.getId(), e);
            }
        });
    }
}
