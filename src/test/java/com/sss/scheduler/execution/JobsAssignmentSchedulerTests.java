package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.service.JobService;
import com.sss.scheduler.tests.JobTestService;
import com.sss.scheduler.utils.TestComparisonUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class JobsAssignmentSchedulerTests {

  @Resource
  private LockManager lockManager;

  @Resource
  private JobService jobService;

  @Resource
  private JobRepository jobRepository;

  @Resource
  private JobTestService jobTestService;

  @Resource
  private JobsAssignmentScheduler jobsAssignmentScheduler;

  @PostConstruct
  public void enableJobsAssignmentScheduler() {
    jobsAssignmentScheduler.enableExecuter();
  }

  @Test
  void executeJobSchedulerSingleJob() {
    jobRepository.deleteAll();
    jobsAssignmentScheduler.assignJobsToExecute();

    String jobName = createNewJob();
    jobsAssignmentScheduler.assignJobsToExecute();

    JobInstance jobInstance = jobTestService.getJobByName(jobName);
    Assert.assertEquals(jobName, jobInstance.getJobName());
    Assert.assertEquals(lockManager.getDefaultLockName(), jobInstance.getExecuteBy());
    TestComparisonUtil.assertDateCloseToNow(jobInstance.getReservedUntil(), Duration.ofSeconds(180));
    jobRepository.deleteAll();
  }

  @Test
  void executeJobsAssignmentSchedulerTest() {
    jobRepository.deleteAll();
    List<String> JobNames = createNewJob(12);
    Assert.assertEquals(JobNames.size(), 12);

    assertAssignedJobs(0);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(10);
    jobRepository.deleteAll();
  }

  @Test
  void executeJobsAssignmentSchedulerIncrementalTest() {
    jobRepository.deleteAll();
    List<String> jobNames = createNewJob(4);
    Assert.assertEquals(jobNames.size(), 4);

    assertAssignedJobs(0);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(4);

    createNewJob(9);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(10);
    jobRepository.deleteAll();
  }

  private String createNewJob() {
    return createNewJob(1).get(0);
  }

  private List<String> createNewJob(int jobsAmounts) {
    List<String> JobNames = new ArrayList<>();
    for(int i = 0; i < jobsAmounts; i++) {
      String JobName = "JobName-" + UUID.randomUUID().toString().substring(0, 4);
      JobInstance jobInstance = new JobInstance();
      jobInstance.setJobName(JobName);
      jobService.createJob(jobInstance);
      JobNames.add(JobName);
    }
    return JobNames;
  }

  private void assertAssignedJobs(long expectedAssignedJobs) {
    Instant now = Instant.now();
    long notAssignedJobs = jobRepository.findAll().stream()
            .filter(job -> job.getExecuteBy() != null)
            .filter(job -> job.getReservedUntil().isAfter(now))
            .filter(job -> job.getCreationDate().isBefore(now))
            .filter(job -> job.getNextExecutionDate().isBefore(now))
            .count();
    Assert.assertEquals(expectedAssignedJobs, notAssignedJobs);
  }

  private void logAllJobs() {
    List<JobInstance> all = jobRepository.findAll();
    all.forEach(cmd -> {
      System.out.println("  CMD: " + cmd);
    });
  }
}
