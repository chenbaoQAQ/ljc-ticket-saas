package com.ljc.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerateMain {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String hash = encoder.encode(rawPassword);

        System.out.println("raw password = " + rawPassword);
        System.out.println("bcrypt hash = " + hash);
    }
}
