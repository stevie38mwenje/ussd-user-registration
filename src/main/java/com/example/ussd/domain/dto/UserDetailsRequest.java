package com.example.ussd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDetailsRequest {
    private String name;
    private String phoneNumber;
    private Integer age;
}
