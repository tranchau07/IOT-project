package com.example.Iot_Project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MqttTopic {
    SENSOR_READING("^hust/[^/]+/[^/]+/[^/]+/up/sensor$"),
    STATE("^hust/[^/]+/[^/]+/[^/]+/up/state$"),
    RESPONSE("^hust/[^/]+/[^/]+/[^/]+/up/control/response$"),
    CONTROL("^hust/[^/]+/[^/]+/[^/]+/down/control$");
    private final String pattern;

    //device -> server : up
    //hust/{building}/{classroomId}/{deviceId}/{direction}/{type}
    //sensor: đo lường
    //state: trạng thái device: power và connectivity: heartbeat

    public static MqttTopic match(String topic){
        for(MqttTopic type : values()){
           if(topic.matches(type.pattern)){
               return type;
           }
        }
        return null;
    }

}
