package com.hellofresh.events.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan({"com.hellofresh.events.statistics"})
public class EventsStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsStatisticsApplication.class, args);
    }
}
