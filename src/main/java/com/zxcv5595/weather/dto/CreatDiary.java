package com.zxcv5595.weather.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.executable.ValidateOnExecution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public class CreatDiary {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        private LocalDate date;

        @NotNull
        @Size(min = 1,max = 500)
        private String text;
    }

}
