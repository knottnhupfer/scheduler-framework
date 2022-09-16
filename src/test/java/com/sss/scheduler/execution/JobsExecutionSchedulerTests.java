package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.tests.*;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest
public class JobsExecutionSchedulerTests extends AbstractJobsExecutionTest {

  @Resource
  private JobRepository jobRepository;

  @Resource
  private JobsExecutionScheduler jobsExecutionScheduler;

  @Test
  public void executeSingleCommand() {
    createNewJob("increaseByOneJob", "schedulerSingleJob");

    JobInstance job = jobRepository.findAllJobsByName("increaseByOneJob").get(0);
    job.setExecuteBy(lockManager.getDefaultLockName());
    job.setExecutions(0L);
    jobRepository.save(job);

    jobsExecutionScheduler.executeAssignedJobs();

    JobInstance executedJob = jobRepository.findById(job.getId()).get();
    Assert.assertNull(executedJob.getExecuteBy());
    Assert.assertNull(executedJob.getReservedUntil());
    Assert.assertNull(executedJob.getExecutionResultMessage());
    Assert.assertNull(executedJob.getNextExecutionDate());

    Assert.assertNotNull(executedJob.getLastExecutionDate());
    Assert.assertNotNull(executedJob.getExecutionDuration());
    Assert.assertNotNull(executedJob.getCreationDate());
    Assert.assertNotNull(executedJob.getPriority());

    Assert.assertEquals(JobStatus.COMPLETED_SUCCESSFUL, executedJob.getStatus());

    Long executedTimes = IncreaseByOneJob.getCounterValue("schedulerSingleJob");
    Assert.assertEquals(Long.valueOf(1), executedTimes);
  }

  @Test
  public void businessExceptionTest() {
    jobRepository.deleteAll();
    createNewJob(BusinessErrorJob.NAME);
    assignCreatedJob(BusinessErrorJob.NAME);
    jobsExecutionScheduler.executeAssignedJobs();

    JobInstance job = jobRepository.findAllJobsByName(BusinessErrorJob.NAME).get(0);
    Assert.assertNull(job.getExecuteBy());
    Assert.assertNull(job.getReservedUntil());
    Assert.assertNull(job.getNextExecutionDate());
    Assert.assertNotNull(job.getLastExecutionDate());
    Assert.assertNotNull(job.getExecutionDuration());

    Assert.assertEquals(Long.valueOf(1L), job.getExecutions());
    Assert.assertEquals(JobStatus.BUSINESS_ERROR, job.getStatus());
    Assert.assertEquals("Business error happened.", job.getExecutionResultMessage());
  }

  @Test
  public void randomRuntimeExceptionTest() {
    jobRepository.deleteAll();
    createNewJob(RuntimeExceptionErrorJob.NAME);
    assignCreatedJob(RuntimeExceptionErrorJob.NAME);
    jobsExecutionScheduler.executeAssignedJobs();

    JobInstance job = jobRepository.findAllJobsByName(RuntimeExceptionErrorJob.NAME).get(0);
    Assert.assertNull(job.getExecuteBy());
    Assert.assertNull(job.getReservedUntil());
    Assert.assertNotNull(job.getNextExecutionDate());
    Assert.assertNotNull(job.getLastExecutionDate());
    Assert.assertNotNull(job.getExecutionDuration());

    Assert.assertEquals(Long.valueOf(1L), job.getExecutions());
    Assert.assertEquals(JobStatus.ERRORNOUS_RETRIGGER, job.getStatus());
    Assert.assertEquals("Runtime exception error happened.", job.getExecutionResultMessage());

//    assignCreatedJob(RuntimeExceptionErrorJob.NAME);
//    jobsExecutionScheduler.executeAssignedJobs();
//    job = jobRepository.findAllJobsByName(RuntimeExceptionErrorJob.NAME).get(0);
//    Assert.assertEquals(Long.valueOf(2L), job.getExecutions());

//    assignCreatedJob(RuntimeExceptionErrorJob.NAME);
//    jobsExecutionScheduler.executeAssignedJobs();
//    job = jobRepository.findAllJobsByName(RuntimeExceptionErrorJob.NAME).get(0);
//    Assert.assertEquals(Long.valueOf(3L), job.getExecutions());
//    Assert.assertEquals(JobStatus.ERRORNOUS_RETRIGGER, job.getStatus());
  }

  @Test
  public void executeSingleCommandWithBusinessObjectId() {
    createNewJob("increaseByOneWithBusinessObjectIdJob", "testBusinessObjectId", 1, 23L);

    JobInstance job = jobRepository.findAllJobsByName("increaseByOneWithBusinessObjectIdJob").get(0);
    job.setExecuteBy(lockManager.getDefaultLockName());
    job.setExecutions(0L);
    jobRepository.save(job);

    jobsExecutionScheduler.executeAssignedJobs();

    Long passedBusinessObjectId = IncreaseByOneWithBusinessObjectIdJob.getBusinessObjectId("testBusinessObjectId");
    Assert.assertEquals(23L, passedBusinessObjectId.longValue());
  }

  @Test
  public void executeSingleCommandWithBusinessObjectIdAmdExecutions() {
    String jobExecutionsName =  "job-id-" + UUID.randomUUID();
    createNewJob("increaseByOneWithBusinessObjectIdAndExecutionsJob", jobExecutionsName, 1, 23L);

    JobInstance job = jobRepository.findAllJobsByName("increaseByOneWithBusinessObjectIdAndExecutionsJob").get(0);
    job.setExecuteBy(lockManager.getDefaultLockName());
    jobRepository.save(job);
    Long jobId = job.getId();

    jobsExecutionScheduler.executeAssignedJobs();

    Long passedBusinessObjectId = IncreaseByOneWithBusinessObjectIdAndExecutionsJob.getJobExecutions(jobExecutionsName);
    Assertions.assertEquals(0L, passedBusinessObjectId.longValue());

    job = jobRepository.findById(jobId).get();
    job.setExecuteBy(lockManager.getDefaultLockName());
    jobRepository.save(job);

    jobsExecutionScheduler.executeAssignedJobs();
    passedBusinessObjectId = IncreaseByOneWithBusinessObjectIdAndExecutionsJob.getJobExecutions(jobExecutionsName);
    Assertions.assertEquals(1L, passedBusinessObjectId.longValue());
  }
}
