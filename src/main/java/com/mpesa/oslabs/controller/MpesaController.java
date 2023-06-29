package com.mpesa.oslabs.controller;

import com.mpesa.oslabs.dtos.*;
import com.mpesa.oslabs.models.C2b_Entries;
import com.mpesa.oslabs.repositories.C2bEntriesRepositories;
import com.mpesa.oslabs.services.DarajaApi;
import com.mpesa.oslabs.sms.Gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;


@RestController
@Slf4j
@RequestMapping("/oslabs")
@PropertySource("classpath:application.properties")
public class MpesaController {

    private final DarajaApi darajaApi;

    private final C2bEntriesRepositories c2bEntriesRepositories;

    @Value("${sms_api_key}")
    private  String smsApiKey;

    @Value("${sms_endpoint}")
    private  String smsEndpoint;

    @Value("${sms_short_code}")
    private String smsShortCode;

    @Value("${sms_patner_id}")
    private  Integer smsPatnerId;

    @Value("${server.port}")
    private  String serverPort;




    public MpesaController(DarajaApi darajaApi, C2bEntriesRepositories c2bEntriesRepositories) {
        this.darajaApi = darajaApi;
        this.c2bEntriesRepositories = c2bEntriesRepositories;
    }

    @GetMapping("/server")
    public  String getServer(){
        return "Server running on port :" + serverPort;
    }

    @GetMapping("/token")
    public ResponseEntity<AcessTokenResponse> getAccessToken(){
        return  ResponseEntity.ok(darajaApi.getAccessToken());
    }

    @PostMapping(value = "/confirmation" , produces = "application/json")
    ResponseEntity<ConfirmationAcknowledgment> mpesaValidation(@RequestBody MpesaValidationResponse mpesaValidationResponse){

        Optional<C2b_Entries> optionalEntry = c2bEntriesRepositories.findByTransactionId(mpesaValidationResponse.getTransID());

        if(optionalEntry.isPresent()){
            log.info("Mpesa transaction with id : " + mpesaValidationResponse.getTransID() + " already Exist");
        }

        C2b_Entries c2BEntries = new C2b_Entries();
        c2BEntries.setTransactionType( mpesaValidationResponse.getTransactionType());
        c2BEntries.setTransactionId(mpesaValidationResponse.getTransID());
        c2BEntries.setMobileNumber(mpesaValidationResponse.getMSISDN());
        c2BEntries.setTransTime(mpesaValidationResponse.getTransTime());
        c2BEntries.setFirstName(mpesaValidationResponse.getFirstName());
        c2BEntries.setMiddleName(mpesaValidationResponse.getMiddleName());
        c2BEntries.setLastName(mpesaValidationResponse.getLastName());
        c2BEntries.setTransactionAmount(mpesaValidationResponse.getTransAmount());
        c2BEntries.setBillRefNumber(mpesaValidationResponse.getBillRefNumber());
        c2BEntries.setOrgAccountBalance(mpesaValidationResponse.getOrgAccountBalance());
        c2BEntries.setBusinessShortCode(mpesaValidationResponse.getBusinessShortCode());
        c2BEntries.setThirdPartyTransID(mpesaValidationResponse.getThirdPartyTransID());
        c2BEntries.setEntryTime( new Date());

        c2bEntriesRepositories.save(c2BEntries);

       sendSms(mpesaValidationResponse.getMSISDN() , mpesaValidationResponse.getTransAmount() , mpesaValidationResponse.getTransID());

        ConfirmationAcknowledgment confirmationAcknowledgment = new ConfirmationAcknowledgment();
        confirmationAcknowledgment.setResultCode("0");
        confirmationAcknowledgment.setResultDesc("Success");

        return  ResponseEntity.ok(confirmationAcknowledgment);
    }


    @PostMapping(value = "/register-url" , produces = "application/json")
    public ResponseEntity<RegisterUrlResponse> registerUl(){
        return ResponseEntity.ok(darajaApi.registerUrl());
    }


    @PostMapping("/c2b-simulations")
    public  ResponseEntity<SimulateC2bResponse> simulateC2b( @RequestBody SimulateC2bRequest simulateC2bRequest){


        return  ResponseEntity.ok(darajaApi.simulateB2cTransactions(simulateC2bRequest));
    }


    public void sendSms(final String phoneNumber , final String amount, final String transactionId){


        Gateway gateway = new Gateway( smsEndpoint,smsPatnerId, smsApiKey , smsShortCode);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Dear customer we have received Ksh.").append(amount).append(" with reference code ").append(transactionId).append(" Thanks.");

        String message = stringBuilder.toString();

        try {
            String res = gateway.sendSingleSms(message, phoneNumber);

            log.info("Message sent successfully  <----> "+ res);
        } catch (IOException e) {
          log.error(" SMS sending failed <----> " + e.getLocalizedMessage());

        }



    }


}
