package com.sss.scheduler.service;

import com.sss.scheduler.controller.JobShort;
import com.sss.scheduler.controller.JobStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class JobsMonitoringService {

  public List<JobShort> getJobs(JobStatus status, Instant startDate, Instant endDate) {
    return Collections.emptyList();
  }
}
