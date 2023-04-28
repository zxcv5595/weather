package com.zxcv5595.weather.service;

import com.zxcv5595.weather.Exception.DiaryException;
import com.zxcv5595.weather.WeatherApplication;
import com.zxcv5595.weather.domain.DateWeather;
import com.zxcv5595.weather.domain.Diary;
import com.zxcv5595.weather.dto.DiaryDto;
import com.zxcv5595.weather.repository.DateWeatherRepository;
import com.zxcv5595.weather.repository.DiaryRepository;
import com.zxcv5595.weather.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
    @Value("${openweathermap.key}")
    private String apiKey;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional
    public void createDiary(LocalDate date, String text) {

        logger.info("start to creat diary");

        if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
            throw new DiaryException(ErrorCode.INVALID_DATE);
        }


        DateWeather dateWeather = getDateWeather(date);

        //파싱한 데이터 db에 넣기
        diaryRepository.save(Diary.builder()
                .temperature(dateWeather.getTemperature())
                .icon(dateWeather.getIcon())
                .weather(dateWeather.getWeather())
                .date(date)
                .text(text)
                .build());
        logger.info("end to creat diary");
    }

     DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.size() == 0) {
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    String getWeatherApi() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();

            return response.toString();
        } catch (Exception e) {
            throw new DiaryException(ErrorCode.FAIL_TO_GET_WEATHER_FROM_API);
        }
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherApi();

        Map<String, Object> parsedWeather = parseWeather(weatherData);
        if (parsedWeather.isEmpty()) {
            return DateWeather.builder().build();
        }

        return DateWeather.builder()
                .date(LocalDate.now())
                .icon(parsedWeather.get("icon").toString())
                .weather(parsedWeather.get("main").toString())
                .temperature((Double) parsedWeather.get("temp"))
                .build();
    }

    Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();
        try {
            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weatherData = (JSONObject) weatherArray.get(0);
            resultMap.put("main", weatherData.get("main"));
            resultMap.put("icon", weatherData.get("icon"));
            JSONObject mainData = (JSONObject) jsonObject.get("main");
            resultMap.put("temp", mainData.get("temp"));
        }catch (NullPointerException e){
            return resultMap;
        }



        return resultMap;


    }

    @Transactional(readOnly = true)
    public List<DiaryDto> readDiary(LocalDate date) {
        if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
            throw new DiaryException(ErrorCode.INVALID_DATE);
        }
        List<Diary> diaries = diaryRepository.findAllByDate(date);

        return diaries.stream()
                .map(DiaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DiaryDto> readDiaries(LocalDate startDate, LocalDate endDate) {
        List<Diary> diaries = diaryRepository.findAllByDateBetween(startDate, endDate);

        return diaries.stream()
                .map(DiaryDto::fromEntity)
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Optional<Diary> diary = diaryRepository.findFirstByDate(date);

        if (diary.isPresent()) {
            Diary newDiary = diary.get();
            newDiary.setText(text);
            diaryRepository.save(newDiary);
        } else {
            throw new DiaryException(ErrorCode.NOT_EXIST_DIARY);
        }


    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }
}
