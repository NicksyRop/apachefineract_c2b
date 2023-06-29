package com.mpesa.oslabs.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.oslabs.dtos.*;
import com.mpesa.oslabs.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class DarajaApiImp implements DarajaApi{

    private  final ObjectMapper objectMapper;

    @Value("${oath_endpoint}")
    private String oathEndpoint;

    @Value("${mpesa_short_code}")
    private String mpesaShortCode;
    @Value("${consumer_secret}")
    private  String consumerSecret;

    @Value("${consumer_key}")
    private  String consumerKey;

    @Value("${confirmation_url}")
    private String confirmationUrl;

    @Value("${register_url_endpoint}")
    private String registerUrlEndpoint;


    @Value("${validation_url}")
    private String validationUrl;

    @Value("${simulate_c2b_transactions}")
    private  String simulateC2bEndpoint;


    public DarajaApiImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AcessTokenResponse getAccessToken() {
       String encodedCredentials = Helper.toBase64Encode( String.format("%s:%s",consumerKey ,consumerSecret ));

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(oathEndpoint)
                .method("GET", null)
                .addHeader("Authorization", String.format("%s %s", "Basic", encodedCredentials))
                .build();
        try {
            Response response = client.newCall(request).execute();

            assert response.body() != null;

            //object mapper readValue method to convert json back to Java object.
            return objectMapper.readValue(response.body().string(), AcessTokenResponse.class);

        } catch (IOException e) {
            log.error("Unable to generate access token " + e.getLocalizedMessage());
            return null;
        }

    }

    @Override
    public RegisterUrlResponse registerUrl() {

        AcessTokenResponse accessTokenResponse = getAccessToken();

        RegisterUrlRequest registerUrlRequest = new RegisterUrlRequest();
        registerUrlRequest.setConfirmationURL(confirmationUrl);
        registerUrlRequest.setShortCode(mpesaShortCode);
        registerUrlRequest.setResponseType("Completed");
        registerUrlRequest.setValidationURL(validationUrl);

        String bodyJson = null;
        try {
            bodyJson = objectMapper.writeValueAsString(registerUrlRequest);
        } catch (JsonProcessingException e) {
           log.error("Unable to convert RegisterUrlRequest to json " + e.getLocalizedMessage());
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, bodyJson);
        Request request = new Request.Builder()
                .url(registerUrlEndpoint)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", String.format("%s %s", "Bearer", accessTokenResponse.getAccessToken()))
                .build();


        try {

            Response response = client.newCall(request).execute();
            assert response.body() != null;

            return objectMapper.readValue(response.body().string(), RegisterUrlResponse.class);

        } catch (IOException e) {
            log.error(String.format("Could not register url -> %s", e.getLocalizedMessage()));
            return null;
        }

    }

    @Override
    public SimulateC2bResponse simulateB2cTransactions(SimulateC2bRequest simulateC2bRequest) {

        AcessTokenResponse acessTokenResponse = getAccessToken();




        String bodyJson = null;
        try {
            bodyJson = objectMapper.writeValueAsString(simulateC2bRequest);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert RegisterUrlRequest to json " + e.getLocalizedMessage());
        }


//        SimulateC2bRequest simulateC2bRequest = new SimulateC2bRequest();
//        simulateC2bRequest.setShortCode(mpesaShortCode);
//        simulateC2bRequest.setCommandID("CustomerBuyGoodsOnline");
//        simulateC2bRequest.setMsisdn("254705912645");
//        simulateC2bRequest.setBillRefNumber("");
//        simulateC2bRequest.setAmount();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType,bodyJson);
        Request request = new Request.Builder()
                .url(simulateC2bEndpoint)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", String.format("%s %s", "Bearer", acessTokenResponse.getAccessToken()))
                .build();


        try {

            Response response = client.newCall(request).execute();
          assert  response.body() != null;

          return objectMapper.readValue(response.body().string() , SimulateC2bResponse.class);


        } catch (IOException e) {
           log.info("Unable to simulate c2b transactions" , e.getLocalizedMessage());
           return null;
        }

    }

}
