package com.zxcv5595.weather.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    NOT_EXIST_DIARY("다이어리가 존재하지 않습니다."),
    FAIL_TO_GET_WEATHER_FROM_API("api 로 부터 날씨 데이터를 가져오는데 실패하였습니다."),
    INTERNAL_SERVER_ERROR("내부 서버오류가 발생하였습니다."),
    INVALID_REQUEST("알맞지 않은 요구 입니다."),
    INVALID_DATE("유효하지 않은 날짜 입니다.");


    String description;
}
