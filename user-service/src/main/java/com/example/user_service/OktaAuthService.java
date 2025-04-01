package com.example.user_service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.user_service.models.User;

@Service
public class OktaAuthService {

    private final String OKTA_URL = "https://dev-74581873.okta.com/api/v1/users";
    
    public void createOktaUser(User user, String accessToken) {
       
        String jsonBody = "{"
                + "\"profile\": {"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"login\": \"" + user.getUsername() + "\","
                + "},"
                + "\"credentials\": {"
                + "\"password\": {"
                + "\"value\": \"" + user.getPassword() + "\""
                + "}"
                + "}"
                + "}";

     
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken); 

        
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

       
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(OKTA_URL, HttpMethod.POST, entity, String.class);

       
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Failed to create Okta user111: " + response.getBody());
        }
    }
}
