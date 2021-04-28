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
import java.util.Optional;

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
    createNewJob("schedulerSingleJob");

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

  private String createNewJob(String counterName) {
    return createNewJob(counterName, 1).get(0);
  }

  private List<String> createNewJob(String counterName, int jobsAmounts) {
    List<String> jobNames = new ArrayList<>();
    for(int i = 0; i < jobsAmounts; i++) {
      String JobName = "increaseByOneJob";
      JobInstance jobInstance = new JobInstance();
      jobInstance.setJobName(JobName);
      jobInstance.setExecutions(0L);

      JobMap jobMap = new JobMap();
      jobMap.putValue(IncreaseByOneJob.COUNTER_NAME, counterName);
      jobInstance.setJobMap(jobMap);
      jobService.createJob(jobInstance);
      jobNames.add(JobName);
    }
    return jobNames;
  }
}
