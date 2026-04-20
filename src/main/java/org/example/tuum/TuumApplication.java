package org.example.tuum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.tuum.mapper")
public class TuumApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuumApplication.class, args);
    }

}
