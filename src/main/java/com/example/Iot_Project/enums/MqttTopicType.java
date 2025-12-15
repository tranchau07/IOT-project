package com.example.Iot_Project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MqttTopicType {
    SENSOR_DATA("devices/.*/sensors/.*/data"),
    STATUS("devices/.*/status"),
    HEARTBEAT("devices/.*/heartbeat"),
    CONTROL_RESPONSE("devices/.*/control/response");

    private final String pattern;

    public static MqttTopicType match(String topic){
        for(MqttTopicType type : values()){
           if(topic.matches(type.pattern)){
               return type;
           }
        }

        return null;
    }

}
