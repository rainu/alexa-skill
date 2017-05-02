package de.rainu.alexa.cloud.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

  public static final String CALENDAR_CALL_POOL = "calendarCallPool";

  @Bean(CALENDAR_CALL_POOL)
  public ExecutorService executer(){
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    return executorService;
  }
}
