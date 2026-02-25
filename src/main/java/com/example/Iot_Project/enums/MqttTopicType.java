package com.example.Iot_Project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MqttTopicType {
    SENSOR_READING("hust/+/+/+/up/sensor"),
    STATE("hust/+/+/+/up/state"),
    RESPONSE("hust/+/+/+/up/control/response");

    final String topic;
}
