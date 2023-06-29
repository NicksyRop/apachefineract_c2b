package com.mpesa.oslabs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Helper {

    public static String toJson(Object object) {

            ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public  static String toBase64Encode(String value){
        byte[] encodedBytes = Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8));
        String base64String = new String(encodedBytes, StandardCharsets.UTF_8);
        return base64String;

    }
}
