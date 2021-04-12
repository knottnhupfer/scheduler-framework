package com.sss.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "scheduler.job-execution")
public class SchedulerConfiguration {

  private Long retries;
  private Long intervalSeconds;
  private String executionGroup;
  private String executionStrategy;
  private List<JobsSchedulerConfiguration> configurations;

  private Integer maxJobsPerNode = 10;
  // if a job hasn't been executed the hostname will be removed after next fetching
  private Long jobsReservationInterval = 90L;
  // interval between a node tries to assign jobs, fetching interval will be jobsAssigningInterval / 2
  private Long jobsAssigningInterval = 30L;
}
