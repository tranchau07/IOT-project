package com.example.Iot_Project.exception;

import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlerRuntimeException(){
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        ApiResponse<?> api = new ApiResponse<>();
        api.setCode(errorCode.getCode());
        api.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(api);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlerRuntimeException(AppException e){
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        ApiResponse<?> api = new ApiResponse<>();
        api.setCode(errorCode.getCode());
        api.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(api);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String message = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;

        try {

            errorCode = ErrorCode.valueOf(message);
        } catch (IllegalArgumentException ignored) {

        }

        ApiResponse<?> api = new ApiResponse<>();
        api.setCode(errorCode.getCode());
        api.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(api);
    }
}
