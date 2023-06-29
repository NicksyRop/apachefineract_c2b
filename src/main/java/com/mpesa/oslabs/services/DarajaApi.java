package com.mpesa.oslabs.services;

import com.mpesa.oslabs.dtos.AcessTokenResponse;
import com.mpesa.oslabs.dtos.RegisterUrlResponse;
import com.mpesa.oslabs.dtos.SimulateC2bRequest;
import com.mpesa.oslabs.dtos.SimulateC2bResponse;

public interface DarajaApi {

    AcessTokenResponse getAccessToken();

    RegisterUrlResponse registerUrl();

    SimulateC2bResponse simulateB2cTransactions(SimulateC2bRequest simulateC2bRequest);
}
