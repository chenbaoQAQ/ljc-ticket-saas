package com.ljc.vo;

import lombok.Data;

@Data
public class LoginVO {
    private Long companyId;
    private Long employeeId;
    private String name;
    private String role;
    private String token;
}
