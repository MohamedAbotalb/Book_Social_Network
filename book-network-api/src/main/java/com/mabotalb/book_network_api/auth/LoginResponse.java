package com.mabotalb.book_network_api.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LoginResponse {

    private String token;
}
