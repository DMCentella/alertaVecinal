package com.alertavecinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthClientResponse {

    private AuthUserData response;
    private ErrorMessage error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthUserData {
        private String token;
        private String username;
        private String message;
        private Long userId;
    }
}
