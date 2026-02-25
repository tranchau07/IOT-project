package com.example.Iot_Project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandStatus {
    CREATE,
    SENT,
    SUCCESS,
    FAILED,
    REJECTED,
    TIMEOUT
    ;
}
