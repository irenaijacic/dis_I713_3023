package com.example.common;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
/* 
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default(); 

    @Override
    public Exception decode(String methodKey, Response response) {
        System.out.println("Feign Client Error! Method: " + methodKey + ", Status: " + response.status());

        if (response.status() == 503) {
            return new CustomException("Service is not available!", "UNAVAILABLE", 503);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
*/public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default(); 

    public Exception decode(String methodKey, Response response) {
        if (response.status() == 503) {
            System.out.println("Service is unavailable (503) for method: " + methodKey);
            // Vraća FeignException koji CircuitBreaker može da prepozna
            return FeignException.errorStatus(methodKey, response);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}   