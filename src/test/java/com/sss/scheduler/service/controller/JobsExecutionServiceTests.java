package com.sss.scheduler.service.controller;

import com.sss.scheduler.execution.TestJobCreator;
import com.sss.scheduler.model.JobShort;
import com.sss.scheduler.model.MonitoringJobState;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.tests.BusinessErrorJob;
import com.sss.scheduler.tests.ErrornousJob;
import com.sss.scheduler.tests.SuccessfulJob;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;

import static org.junit.Assert.*;

@SpringBootTest
public class JobsExecutionServiceTests {

  @Resource
  private JobRepository jobRepository;

  @Resource
  private TestJobCreator testJobCreator;

  @Resource
  private JobsTriggerService jobsTriggerService;

  @Resource
  private JobsMonitoringService jobsMonitoringService;

  @Test
  void retriggerJobByJobMapParameter() {
    jobRepository.deleteAll();
    testJobCreator.createJobsAndExecute(BusinessErrorJob.NAME, ErrornousJob.NAME, SuccessfulJob.NAME);

    JobShort job = retrieveJobWithState(MonitoringJobState.pending);
    assertEquals(1L, job.getExecutions().longValue());
    jobsTriggerService.triggerJobByJobMapParam(ErrornousJob.NAME, TestJobCreator.PARAM_KEY_BUSINESS_OBJECT_ID, "3", false,false);
    job = retrieveJobWithState(MonitoringJobState.pending);
    assertEquals(2L, job.getExecutions().longValue());

    job = retrieveJobWithState(MonitoringJobState.failed);
    assertEquals(1L, job.getExecutions().longValue());
    try {
      jobsTriggerService.triggerJobByJobMapParam(BusinessErrorJob.NAME, TestJobCreator.PARAM_KEY_BUSINESS_OBJECT_ID, "3", false,false);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Trying to execute JobInstance(id:"));
      assertTrue(e.getMessage().contains(") with final state:BUSINESS_ERROR"));
      job = retrieveJobWithState(MonitoringJobState.failed);
      assertEquals(1L, job.getExecutions().longValue());
    }

    jobsTriggerService.triggerJobByJobMapParam(BusinessErrorJob.NAME, TestJobCreator.PARAM_KEY_BUSINESS_OBJECT_ID, "3", true,false);
    job = retrieveJobWithState(MonitoringJobState.failed);
    assertEquals(2L, job.getExecutions().longValue());

    jobsTriggerService.triggerJobByJobMapParam(BusinessErrorJob.NAME, TestJobCreator.PARAM_KEY_BUSINESS_OBJECT_ID, "4", true,true);
    job = retrieveJobWithState(MonitoringJobState.failed);
    assertEquals(2L, job.getExecutions().longValue());
  }

  @Test
  void retriggerJobByBusinessObjectId() {
    jobRepository.deleteAll();
    testJobCreator.createJobsAndExecute(BusinessErrorJob.NAME, ErrornousJob.NAME, SuccessfulJob.NAME);

    JobShort job = retrieveJobWithState(MonitoringJobState.pending);
    assertEquals(1L, job.getExecutions().longValue());
    jobsTriggerService.triggerJobByBusinessObjectId(ErrornousJob.NAME,3L, false,false);
    job = retrieveJobWithState(MonitoringJobState.pending);
    assertEquals(2L, job.getExecutions().longValue());

    job = retrieveJobWithState(MonitoringJobState.failed);
    assertEquals(1L, job.getExecutions().longValue());
    try {
      jobsTriggerService.triggerJobByBusinessObjectId(BusinessErrorJob.NAME,3L, false,false);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Trying to execute JobInstance(id:"));
      assertTrue(e.getMessage().contains(") with final state:BUSINESS_ERROR"));
      job = retrieveJobWithState(MonitoringJobState.failed);
      assertEquals(1L, job.getExecutions().longValue());
    }

    jobsTriggerService.triggerJobByBusinessObjectId(BusinessErrorJob.NAME,3L, true,false);
    job = retrieveJobWithState(MonitoringJobState.failed);
    assertEquals(2L, job.getExecutions().longValue());

    jobsTriggerService.triggerJobByBusinessObjectId(BusinessErrorJob.NAME,4L, true,true);
    job = retrieveJobWithState(MonitoringJobState.failed);
    assertEquals(2L, job.getExecutions().longValue());
  }

  @Test
  void retriggerJobsInBatchMode() {
    jobRepository.deleteAll();
    testJobCreator.createJobsAndExecute(ErrornousJob.NAME, ErrornousJob.NAME);

    JobShort job = retrieveJobWithState(MonitoringJobState.pending);
    assertEquals(1L, job.getExecutions().longValue());
    try {
      jobsTriggerService.triggerJobByBusinessObjectId(ErrornousJob.NAME,3L, false,false);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Unable to run single job, found 2 jobs.", e.getMessage());
      job = retrieveJobWithState(MonitoringJobState.pending);
      assertEquals(1L, job.getExecutions().longValue());
    }

    jobsTriggerService.triggerJobByBusinessObjectId(ErrornousJob.NAME,3L, false,true);
    job = retrieveJobWithState(MonitoringJobState.pending);
    assertEquals(2L, job.getExecutions().longValue());
  }

  private JobShort retrieveJobWithState(MonitoringJobState monitoringState) {
    Instant startDate = Instant.now().minusSeconds(60);
    return jobsMonitoringService.getJobs(monitoringState, startDate, Instant.now()).get(0);
  }
}
