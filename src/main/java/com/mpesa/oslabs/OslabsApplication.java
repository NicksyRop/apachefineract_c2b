package com.mpesa.oslabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OslabsApplication {

	public ObjectMapper getObjectMapper(){

		return  new ObjectMapper();
	}


	public static void main(String[] args) {
		SpringApplication.run(OslabsApplication.class, args);
	}

}
