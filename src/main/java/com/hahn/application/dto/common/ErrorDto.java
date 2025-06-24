package com.hahn.application.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorDto {
    private int status;
    private String message;
    private long timestamp;
    private List<String> details;

    public static ErrorDto of(HttpStatus status, String message) {
        return ErrorDto.builder()
                .status(status.value())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ErrorDto of(HttpStatus status, String message, List<String> details) {
        return ErrorDto.builder()
                .status(status.value())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .details(details)
                .build();
    }
}
