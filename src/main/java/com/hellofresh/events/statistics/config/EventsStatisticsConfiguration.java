package com.hellofresh.events.statistics.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class EventsStatisticsConfiguration {

    /**
     * Dedicated Thread Modeling to handle POST Requests for incoming
     * Events
     */
    @Bean
    public ExecutorService eventsPostExecutorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("eventsPostExecutor-%d")
                .setDaemon(true)
                .build();
        ExecutorService es = Executors.newFixedThreadPool(2, threadFactory);
        return es;
    }

    /**
     * Dedicated Thread Modeling to handle GET events statistics
     */
    @Bean()
    public ExecutorService eventsStatisticsThreadExecutorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("eventsStatisticsExecutor-%d")
                .setDaemon(true)
                .build();
        ExecutorService es = Executors.newFixedThreadPool(2, threadFactory);
        return es;
    }


}

