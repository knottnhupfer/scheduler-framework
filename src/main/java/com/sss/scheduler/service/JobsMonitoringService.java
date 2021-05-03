package com.sss.scheduler.service;

import com.sss.scheduler.controller.JobShort;
import com.sss.scheduler.controller.JobStatus;
import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.repository.JobRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobsMonitoringService {

  private static final int DEFAULT_MAX_JOBS = 100;

  @Resource
  private JobRepository jobRepository;

  public List<JobShort> getJobs(JobStatus status, Instant startDate, Instant endDate) {
    List<JobInstance> jobs = jobRepository.findJobsByStates(convertStatus(status), startDate, endDate, DEFAULT_MAX_JOBS);
    return jobs.stream().map(JobShort::of).collect(Collectors.toList());
  }

  private List<com.sss.scheduler.domain.JobStatus> convertStatus(JobStatus status) {
    switch (status) {
      case failed:
        return Arrays.asList(
                com.sss.scheduler.domain.JobStatus.BUSINESS_ERROR,
                com.sss.scheduler.domain.JobStatus.COMPLETED_ERRONEOUS);
      case pending:
        return Arrays.asList(
                com.sss.scheduler.domain.JobStatus.ERRORNOUS_RETRIGGER,
                com.sss.scheduler.domain.JobStatus.OPEN,
                com.sss.scheduler.domain.JobStatus.IN_PROGRESS);
      default:
        throw new IllegalStateException("Unknown status: " + status);
    }
  }
}
