package com.zxcv5595.weather.Exception;

import com.zxcv5595.weather.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public DiaryException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
