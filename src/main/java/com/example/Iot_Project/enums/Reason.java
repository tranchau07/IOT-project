package com.example.Iot_Project.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public enum Reason {
    ROOM_EMPTY,
    SCHEDULE_START,
    SCHEDULE_END,
    MANUAL_OVERRIDE,
    SAFETY_VOLTAGE
}
