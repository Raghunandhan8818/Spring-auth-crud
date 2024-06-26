package org.example.simplecrudapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.simplecrudapp.entity.User;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String city;
    private String role;
    private String password;
    private User user;
    private List<User> users;


}

// ToDo: Create Seperate Req and Res DTO
// ToDO: Create Multiple DTOs , ClassName