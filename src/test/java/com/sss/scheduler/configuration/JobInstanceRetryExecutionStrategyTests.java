package com.sss.scheduler.configuration;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.execution.JobsExecutionScheduler;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.service.JobService;
import com.sss.scheduler.tests.JobTestService;
import com.sss.scheduler.utils.TestComparisonUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SpringBootTest
class JobInstanceRetryExecutionStrategyTests {

  private static String KEY_RETRIES = "executedRetries";

  public final static String JOB_NAME = "failingJob";
  public final static String JOB_NAME_FIBONACCI = "secondFailingJob";

  @Resource
  private JobService jobService;

  @Resource
  private LockManager lockManager;

  @Resource
  private JobRepository jobRepository;

  @Resource
  private JobTestService jobTestService;

  @Resource
  private JobsExecutionScheduler jobsExecutionScheduler;

  @Test
  void fibonacciCalculationFirstRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME_FIBONACCI, 0);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME_FIBONACCI);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(30));
  }

  @Test
  void fibonacciCalculationSecondRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME_FIBONACCI, 1);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME_FIBONACCI);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(60));
  }

  @Test
  void fibonacciCalculationThirdRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME_FIBONACCI, 2);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME_FIBONACCI);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(90));
  }

  @Test
  void fibonacciCalculationFourthRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME_FIBONACCI, 3);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME_FIBONACCI);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(150));
  }

  @Test
  void fibonacciCalculationFifthRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME_FIBONACCI, 4);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME_FIBONACCI);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(240));
  }

  @Test
  void simpleCalculationFirstRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME, 0);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(60));
  }

  @Test
  void simpleCalculationSecondRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME, 1);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME);

    Assert.assertNull(fetchedJobInstance.getExecuteBy());
    Assert.assertNull(fetchedJobInstance.getReservedUntil());
    Assert.assertNotNull(fetchedJobInstance.getPriority());
    Assert.assertNotNull(fetchedJobInstance.getCreationDate());
    Assert.assertNotNull(fetchedJobInstance.getExecutionDuration());
    Assert.assertNotNull(fetchedJobInstance.getLastExecutionDate());
    Assert.assertNotNull(fetchedJobInstance.getExecutionResultMessage());
    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(120));
  }

  @Test
  void simpleCalculationThirdRetry() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME, 2);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME);

    Assert.assertEquals("verify correct status", JobStatus.ERRORNOUS_RETRIGGER, fetchedJobInstance.getStatus());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(180));
  }

  @Test
  void verifyProcessingTerminatesTest() {
    cleanupJobs();
    createJobsWithNameAndUpdateExecutionParameters(JOB_NAME, 4);
    JobInstance fetchedJobInstance = jobTestService.getJobByName(JOB_NAME);

    Assert.assertNull("verify nextExecutionDate is null", fetchedJobInstance.getNextExecutionDate());

  }

  private void cleanupJobs() {
    jobRepository.deleteAll();
  }

  private void createJobsWithNameAndUpdateExecutionParameters(String jobName, long retries) {
    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(jobName);
    JobMap jobMap = new JobMap();
    jobMap.putLongValue(KEY_RETRIES, retries);
    jobInstance.setJobMap(jobMap);
    jobService.createJob(jobInstance);

    List<JobInstance> jobs = jobRepository.findAllJobsByName(jobName);
    jobs.stream().forEach(job -> {
      job.setExecuteBy(lockManager.getDefaultLockName());
      job.setReservedUntil(Instant.now().plusSeconds(10));
      jobRepository.save(job);
    });
    jobsExecutionScheduler.executeAssignedJobs();
    jobs = jobRepository.findAllJobsByName(jobName);
    System.out.println("TEST");
  }
}
