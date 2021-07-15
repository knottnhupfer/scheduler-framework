package com.sss.scheduler.service.controller;

import com.sss.scheduler.model.JobShort;
import com.sss.scheduler.model.JobStatus;
import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.repository.JobRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobsMonitoringService {

  private static final int DEFAULT_MAX_JOBS = 100;

  @Resource
  private JobRepository jobRepository;

  public List<JobShort> getJobs(JobStatus status, Instant startDate, Instant endDate) {
    List<JobInstance> jobs = jobRepository.findJobsByStates(JobStatus.convertStatus(status), startDate, endDate, DEFAULT_MAX_JOBS);
    return jobs.stream().map(JobShort::of).collect(Collectors.toList());
  }
}
