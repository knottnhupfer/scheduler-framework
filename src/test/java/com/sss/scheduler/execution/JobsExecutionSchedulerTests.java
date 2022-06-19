package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.service.JobService;
import com.sss.scheduler.tests.IncreaseByOneJob;
import com.sss.scheduler.tests.IncreaseByOneWithBusinessObjectIdJob;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
    createNewJob("businessErrorJob", "dummyCounter");
    assignCreatedJob("businessErrorJob");
    jobsExecutionScheduler.executeAssignedJobs();

    JobInstance job = jobRepository.findAllJobsByName("businessErrorJob").get(0);
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

  private void assignCreatedJob(String jobName) {
    JobInstance job = jobRepository.findAllJobsByName(jobName).get(0);
    job.setExecuteBy(lockManager.getDefaultLockName());
    job.setExecutions(0L);
    jobRepository.save(job);
  }
}
