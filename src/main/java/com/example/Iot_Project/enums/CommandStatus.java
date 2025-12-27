package com.example.Iot_Project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandStatus {
    CREATED,
    SENT,
    SUCCESS,
    FAILED,
    TIMEOUT
    ;
}
