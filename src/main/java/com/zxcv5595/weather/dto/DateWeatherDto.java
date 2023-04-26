package com.zxcv5595.weather.dto;

import com.zxcv5595.weather.domain.DateWeather;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateWeatherDto {

    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;

    public static DateWeatherDto fromEntity(DateWeather dateWeather){
        return DateWeatherDto.builder()
                .date(dateWeather.getDate())
                .weather(dateWeather.getWeather())
                .icon(dateWeather.getIcon())
                .temperature(dateWeather.getTemperature())
                .build();
    }
}
