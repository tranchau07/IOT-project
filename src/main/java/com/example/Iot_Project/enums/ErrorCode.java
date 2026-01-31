package com.example.Iot_Project.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password is invalid", HttpStatus.BAD_REQUEST),
    INVALID_MESSAGE_KEY(1004, "Key is invalid", HttpStatus.BAD_REQUEST),
    USER_DID_NOT_EXIST(1005, "User did not exist", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "Unauthorized", HttpStatus.FORBIDDEN),
    ROLE_EXISTED(2001, "Role is existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(2001, "Role is not existed", HttpStatus.BAD_REQUEST),

    PERMISSION_EXISTED(2001, "Permission is existed", HttpStatus.BAD_REQUEST),



    //Topic exception
    DEVICE_NOT_EXISTED(2001, "Device is not existed", HttpStatus.NOT_FOUND),
    DEVICE_OFFLINE(2002, "Device is offline", HttpStatus.BAD_REQUEST),
    COMMAND_NOT_EXISTED(2003, "Command is not existed", HttpStatus.NOT_FOUND)

    ;
    int code;
    String message;
    HttpStatusCode statusCode;
}
