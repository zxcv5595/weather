package com.zxcv5595.weather.dto;

import com.zxcv5595.weather.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;
}
