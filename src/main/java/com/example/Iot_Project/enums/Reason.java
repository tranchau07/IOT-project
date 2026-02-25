package com.example.Iot_Project.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public enum Reason {
    TEMP_HIGH,
    ROOM_EMPTY,
    OCCUPANCY_LOW,
    SCHEDULE_START,
    MANUAL_OVERRIDE
}
