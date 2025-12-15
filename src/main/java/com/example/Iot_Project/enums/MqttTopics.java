package com.example.Iot_Project.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public enum MqttTopics {
    DEVICE_SENSOR_DATA("devices/+/sensors/+/data"),
    DEVICE_STATUS("devices/+/status"),
    DEVICE_HEARTBEAT("devices/+/heartbeat"),
    DEVICE_CONTROL_RESPONSE("devices/+/control/response");

    final String topic;

}
