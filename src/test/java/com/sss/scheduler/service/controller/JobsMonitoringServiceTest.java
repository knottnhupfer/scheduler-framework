package com.sss.scheduler.service.controller;

import com.sss.scheduler.model.JobShort;
import com.sss.scheduler.model.MonitoringJobState;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.tests.BusinessErrorJob;
import com.sss.scheduler.tests.ErrornousJob;
import com.sss.scheduler.tests.SuccessfulJob;
import com.sss.scheduler.execution.TestJobCreator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SpringBootTest
public class JobsMonitoringServiceTest {

  @Resource
  private JobRepository jobRepository;

  @Resource
  private TestJobCreator testJobCreator;

  @Resource
  private JobsMonitoringService jobsMonitoringService;

  @Test
  public void simpleJobsMonitoringControllerTest() {
    jobRepository.deleteAll();
    Instant startDate = Instant.now().minus(Duration.ofDays(1));
    Instant endDate = Instant.now().plus(Duration.ofDays(1));
    List<JobShort> jobs = jobsMonitoringService.getJobs(MonitoringJobState.failed, startDate, endDate);
    Assert.assertEquals(0, jobs.size());
  }

  @Test
  public void jobsMonitoringControllerTest() {
    jobRepository.deleteAll();
    testJobCreator.createJobsAndExecute(BusinessErrorJob.NAME, ErrornousJob.NAME, SuccessfulJob.NAME);
    Instant startDate = Instant.now().minus(Duration.ofDays(1));
    Instant endDate = Instant.now().plus(Duration.ofDays(1));
    List<JobShort> jobs = jobsMonitoringService.getJobs(MonitoringJobState.failed, startDate, endDate);
    Assert.assertEquals(1, jobs.size());
    jobs = jobsMonitoringService.getJobs(MonitoringJobState.pending, startDate, endDate);
    Assert.assertEquals(1, jobs.size());
  }
}
