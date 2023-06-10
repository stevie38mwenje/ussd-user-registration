package com.example.ussd.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDetailsResponse {
    private String name;
    private String phoneNumber;
    private Integer age;
}
