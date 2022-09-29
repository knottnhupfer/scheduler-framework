package com.sss.scheduler.execution;

import com.sss.scheduler.JobConstants;
import com.sss.scheduler.config.SchedulerConfiguration;
import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.service.JobService;
import com.sss.scheduler.tests.JobTestService;
import com.sss.scheduler.utils.TestComparisonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
  private SchedulerConfiguration schedulerConfiguration;

  @Resource
  private JobsAssignmentScheduler jobsAssignmentScheduler;

  @PostConstruct
  public void enableJobsAssignmentScheduler() {
    jobsAssignmentScheduler.enableExecuter();
  }

  @Before
  public void prepareTest() {
    jobRepository.deleteAll();
  }

  @After
  public void cleanupTest() {
    jobRepository.deleteAll();
  }

  @Test
  void executeJobSchedulerSingleJob() {
    jobRepository.deleteAll();
    jobsAssignmentScheduler.assignJobsToExecute();

    String jobName = createNewJob();
    jobsAssignmentScheduler.assignJobsToExecute();

    JobInstance jobInstance = jobTestService.getJobByName(jobName);
    Assertions.assertEquals(jobName, jobInstance.getJobName());
    Assertions.assertEquals(lockManager.getDefaultLockName(), jobInstance.getExecuteBy());
    TestComparisonUtil.assertDateCloseToNow(jobInstance.getReservedUntil(), Duration.ofSeconds(180));
    jobRepository.deleteAll();
  }

  @Test
  void executeJobsAssignmentSchedulerTest() {
    jobRepository.deleteAll();
    List<String> JobNames = createNewJob(12);
    Assertions.assertEquals(JobNames.size(), 12);

    assertAssignedJobs(0);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(10);
    jobRepository.deleteAll();
  }

  @Test
  void executeJobsAssignmentSchedulerIncrementalTest() {
    jobRepository.deleteAll();
    List<String> jobNames = createNewJob(4);
    Assertions.assertEquals(jobNames.size(), 4);

    assertAssignedJobs(0);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(4);

    createNewJob(9);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(10);
    jobRepository.deleteAll();
  }

  @Test
  void assignJobsAccordingToPriorityTest() {
    createJob(15,"low-priority-job", null, null);
    createJob(12, "high-priority-job", 150, Instant.now().plusSeconds(60 * 5));

    assertAssignedJobs(0);
    jobsAssignmentScheduler.assignJobsToExecute();
    assertAssignedJobs(10);
    List<JobInstance> jobsNotHighPrio = getAllAssignedJobs().stream()
            .filter(job -> !job.getJobName().contains("high-priority-job")).collect(Collectors.toList());
    Assertions.assertEquals(0, jobsNotHighPrio.size());
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
    sleepShort();
    Instant now = Instant.now();
    List<JobInstance> collect = jobRepository.findAll().stream()
            .filter(job -> job.getExecuteBy() != null)
            .filter(job -> job.getReservedUntil().isAfter(now)).collect(Collectors.toList());
    Assertions.assertEquals(expectedAssignedJobs, collect.size());
  }

  private List<JobInstance> getAllAssignedJobs() {
    sleepShort();
    Instant now = Instant.now();
    return jobRepository.findAll().stream()
            .filter(job -> job.getExecuteBy() != null)
            .filter(job -> job.getReservedUntil().isAfter(now)).collect(Collectors.toList());
  }

  private void createJob(String jobName, Integer priority, Instant creationDate) {
    createJob(1, jobName, priority, creationDate);
  }

  private void createJob(int times, String jobName, Integer priority, Instant creationDate) {
    for (int i = 0; i < times; i++) {
      String name = ObjectUtils.isEmpty(jobName) ? "JobName-" + UUID.randomUUID().toString().substring(0, 4) : jobName;

      JobInstance jobInstance = new JobInstance();
      jobInstance.setJobName(name + "_" + i);
      jobInstance.setCreationDate(ObjectUtils.isEmpty(creationDate) ? Instant.now() : creationDate);
      jobInstance.setPriority(ObjectUtils.isEmpty(priority) ? JobConstants.JOB_PRIORITY_MIDDLE : priority);
      jobService.createJob(jobInstance);
    }
  }

  private void sleepShort() {
    try {
      Thread.sleep(200L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
