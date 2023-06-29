package com.mpesa.oslabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class SimulateC2bResponse{

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("ResponseDescription")
	private String responseDescription;

	@JsonProperty("OriginatorCoversationID")
	private String originatorCoversationID;
}