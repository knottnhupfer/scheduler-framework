package com.sss.scheduler.controller;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class JobShort {

  private Long id;
  private String jobName;
  private Instant creationDate;
  private Long executions;
  private Instant nextExecutionDate;
  private String executionResultMessage;
  private JobStatus status;

  public static JobShort of(JobInstance job) {
    JobShort jobShort = new JobShort();
    jobShort.setId(job.getId());
    jobShort.setJobName(job.getJobName());
    jobShort.setCreationDate(job.getCreationDate());
    jobShort.setExecutions(job.getExecutions());
    jobShort.setNextExecutionDate(job.getNextExecutionDate());
    jobShort.setExecutionResultMessage(job.getExecutionResultMessage());
    jobShort.setStatus(job.getStatus());
    return jobShort;
  }
}
