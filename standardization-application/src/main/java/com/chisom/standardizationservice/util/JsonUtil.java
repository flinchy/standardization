package com.chisom.standardizationservice.util;

import com.chisom.standardizationservice.adapters.LocalDateTimeAdapter;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {

    public static GsonBuilder builder() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
    }
}