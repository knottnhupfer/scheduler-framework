package com.sss.scheduler.controller;

import com.sss.scheduler.model.JobShort;
import com.sss.scheduler.model.MonitoringJobState;
import com.sss.scheduler.model.JobsReponse;
import com.sss.scheduler.service.controller.JobsMonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
public abstract class JobsMonitoringController {

  @Resource
  private JobsMonitoringService jobsMonitoringService;

  @GetMapping(value = "/api/jobs/{status}", produces = "application/json")
  public ResponseEntity<JobsReponse> getJobs(@PathVariable("status") MonitoringJobState status,
                                             @RequestParam(value = "startDate", required = false) OffsetDateTime startDate,
                                             @RequestParam(value = "endDate", required = false) OffsetDateTime endDate) {
    log.debug("Retrieve jobs for state:{} from:{} to:{}", status, startDate, endDate);

    Instant start = startDate != null ? startDate.toInstant() : Instant.now().minus(Duration.ofDays(1));
    Instant end = endDate != null ? endDate.toInstant() : Instant.now();
    List<JobShort> jobs = jobsMonitoringService.getJobs(status, start, end);
    return ResponseEntity.ok(JobsReponse.builder().jobs(jobs).build());
  }
}
