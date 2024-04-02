package com.a502.backend.domain.account.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccountValidPasswordRequest {
    String accountNumberOut;
    List<Integer> password;
}
