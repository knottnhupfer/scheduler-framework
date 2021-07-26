package com.sss.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "scheduler.job-execution")
public class SchedulerConfiguration {

  private Long retries = 3L;
  private boolean enabled = true;
  private Long intervalSeconds = 60L;
  private String executionGroup = "global";
  private String executionStrategy = "SimpleRetry";
  private List<JobsSchedulerConfiguration> configurations = new ArrayList<>();

  private Integer maxJobsPerNode = 10;
  // if a job hasn't been executed the hostname will be removed after next fetching
  private Long jobsReservationInterval = 90L;
  // interval between a node tries to assign jobs, fetching interval will be jobsAssigningInterval / 2
  private Long jobsAssigningInterval = 15_000L;
  private Long jobsExecutionInterval = 30_000L;

  private Long jobsAgeCleanupInterval = 500_000L; // cleanup every 5 minutes
  private Long jobsAgeCleanupMinutes = 43_200L; // default 30 days
}
