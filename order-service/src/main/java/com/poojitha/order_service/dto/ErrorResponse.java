package com.poojitha.order_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    public LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
