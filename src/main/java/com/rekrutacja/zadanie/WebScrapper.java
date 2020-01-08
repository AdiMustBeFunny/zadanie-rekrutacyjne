package com.rekrutacja.zadanie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


public class WebScrapper {

    private static final String URL = "http://api.nbp.pl/api/exchangerates/rates/c/usd/<dateFrom>/<today>/?format=json";

    public static Optional<BNPResponseDTO> getDataFromBNP(LocalDate dateFrom) {

        LocalDate today = LocalDate.now();

        String todayMonthString = today.getMonthValue()+"";
        String todayDayString = today.getDayOfMonth()+"";
        String dateFromMonthString = dateFrom.getMonthValue()+"";
        String dateFromDayString = dateFrom.getDayOfMonth()+"";

        if(today.getDayOfMonth() < 10)
            todayDayString = "0"+today.getDayOfMonth();
        if(today.getMonthValue() < 10)
            todayMonthString = "0"+today.getMonthValue();
        if(dateFrom.getDayOfMonth() < 10)
            dateFromDayString = "0"+dateFrom.getDayOfMonth();
        if(dateFrom.getMonthValue() < 10)
            dateFromMonthString = "0"+dateFrom.getMonthValue();

        String modifiedURL = URL
                .replace("<today>", today.getYear()+"-"+todayMonthString+"-"+todayDayString)
                .replace("<dateFrom>",dateFrom.getYear()+"-"+dateFromMonthString+"-"+dateFromDayString);

        Optional<String> response = getResponseAsString(modifiedURL);

        if (response.isPresent()) return Optional.ofNullable(parseResponse(response.get()).orElse(null));
        else return Optional.ofNullable(null);

    }

    private static Optional<String> getResponseAsString(String url) {
        OkHttpClient client = new OkHttpClient();
        String returnString = null;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            returnString = response.body().string();
        } catch (IOException ioex) {
            System.out.println("Failed to get response from provided url(" + url + ").");
        }

        return Optional.ofNullable(returnString);
    }

    private static Optional<BNPResponseDTO> parseResponse(String response) {
        BNPResponseDTO responseDTO = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JSR310Module());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            responseDTO = objectMapper.readValue(response, BNPResponseDTO.class);
        } catch (JsonProcessingException jpex) {
            System.out.println(jpex.getMessage());
        }

        return Optional.ofNullable(responseDTO);
    }


}
