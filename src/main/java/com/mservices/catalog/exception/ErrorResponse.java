package com.mservices.catalog.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ErrorResponse {

    private String code;
    private String message;
    private LocalDateTime time;
}
