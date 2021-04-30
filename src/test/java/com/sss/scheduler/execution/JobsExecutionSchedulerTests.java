package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.service.JobService;
import com.sss.scheduler.tests.IncreaseByOneJob;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class JobsExecutionSchedulerTests {

  @Resource
  private JobService jobService;

  @Resource
  private LockManager lockManager;

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

    Assert.assertEquals(JobStatus.BUSINESS_ERROR, job.getStatus());
    Assert.assertEquals("Business error happened.", job.getExecutionResultMessage());
  }

  private String createNewJob(String jobName, String counterName) {
    return createNewJob(jobName, counterName, 1).get(0);
  }

  private List<String> createNewJob(String jobName, String counterName, int jobsAmounts) {
    List<String> jobNames = new ArrayList<>();
    for(int i = 0; i < jobsAmounts; i++) {
      JobInstance jobInstance = new JobInstance();
      jobInstance.setJobName(jobName);

      JobMap jobMap = new JobMap();
      jobMap.putValue(IncreaseByOneJob.COUNTER_NAME, counterName);
      jobInstance.setJobMap(jobMap);
      jobService.createJob(jobInstance);
      jobNames.add(jobName);
    }
    return jobNames;
  }

  private void assignCreatedJob(String jobName) {
    JobInstance job = jobRepository.findAllJobsByName(jobName).get(0);
    job.setExecuteBy(lockManager.getDefaultLockName());
    job.setExecutions(0L);
    jobRepository.save(job);
  }
}
