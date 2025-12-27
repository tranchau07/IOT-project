package com.example.Iot_Project.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    USER_EXISTED(1001, "User existed"),
    USERNAME_INVALID(1002, "Username is invalid"),
    PASSWORD_INVALID(1003, "Password is invalid"),
    INVALID_MESSAGE_KEY(1004, "Key is invalid"),
    USER_DID_NOT_EXIST(1005, "User did not exist"),
    UNAUTHENTICATED(1006, "Unauthenticated"),


    //Topic exception
    DEVICE_NOT_EXISTED(2001, "Device is not existed"),
    DEVICE_OFFLINE(2002, "Device is offline"),
    COMMAND_NOT_EXISTED(2003, "Command is not existed")

    ;
    int code;
    String message;
}
