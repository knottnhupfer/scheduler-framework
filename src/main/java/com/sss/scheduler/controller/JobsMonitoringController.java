package com.sss.scheduler.controller;

import com.sss.scheduler.service.JobsMonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
public abstract class JobsMonitoringController {

  @Resource
  private JobsMonitoringService jobsMonitoringService;

  @GetMapping(value = "/api/jobs/{status}", produces = "application/json")
  public ResponseEntity<JobsReponse> getJobs(@PathVariable("status") JobStatus status,
                     @RequestParam("startDate") OffsetDateTime startDate, @RequestParam("endDate") OffsetDateTime endDate) {
    log.debug("Retrieve jobs for state:{} from:{} to:{}", status, startDate, endDate);
    List<JobShort> jobs = jobsMonitoringService.getJobs(status, startDate.toInstant(), endDate.toInstant());
    return ResponseEntity.ok(JobsReponse.builder().jobs(jobs).build());
  }
}
