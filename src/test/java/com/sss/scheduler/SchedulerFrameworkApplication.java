package com.sss.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SchedulerFrameworkApplication {

	public static void main(String[] args) {
    ConfigurableApplicationContext application = SpringApplication.run(SchedulerFrameworkApplication.class, args);
    application.start();
  }
}
