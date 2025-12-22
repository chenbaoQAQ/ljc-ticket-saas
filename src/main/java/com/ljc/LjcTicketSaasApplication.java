package com.ljc;

import org.mybatis.spring.annotation.MapperScan; // 确认导入了这个包 📦
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ljc.mapper") // 💡 重点：手动指定 Mapper 所在的包路径
public class LjcTicketSaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(LjcTicketSaasApplication.class, args);
    }
}