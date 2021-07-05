package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.repository.JobRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
public class JobsCleanupExecutionSchedulerTests {

  @Resource
  private JobRepository jobRepository;

  @Resource
  private JobsCleanupExecutionScheduler jobsCleanupExecutionScheduler;

  @Test
  public void cleanupSuccessfulExecutedJobs() {
    jobRepository.deleteAll();
    createJobInstance("SUCCEEDED_KEEP", 4L, JobStatus.COMPLETED_SUCCESSFUL);
    createJobInstance("SUCCEEDED_DELETE", 6L, JobStatus.COMPLETED_SUCCESSFUL);
    jobsCleanupExecutionScheduler.executeCleanupSucceededJobs();

    List<JobInstance> remainingJobs = jobRepository.findAll();
    Assert.assertEquals(remainingJobs.size(), 1);
    Assert.assertEquals(remainingJobs.get(0).getJobName(), "SUCCEEDED_KEEP");
  }

  @Test
  public void cleanupExecutedJobs() {
    jobRepository.deleteAll();
    createJobInstance("SUCCEEDED_KEEP", 4L, JobStatus.COMPLETED_SUCCESSFUL);

    createJobInstance("OPEN_KEEP", 6L, JobStatus.OPEN);
    createJobInstance("IN_PROGRESS_KEEP", 6L, JobStatus.IN_PROGRESS);
    createJobInstance("CANCELLED_KEEP", 6L, JobStatus.CANCELLED);
    createJobInstance("ERRORNOUS_RETRIGGER_KEEP", 6L, JobStatus.ERRORNOUS_RETRIGGER);
    createJobInstance("COMPLETED_ERRONEOUS_KEEP", 6L, JobStatus.COMPLETED_ERRONEOUS);
    createJobInstance("SUCCEEDED_DELETE", 6L, JobStatus.COMPLETED_SUCCESSFUL);
    createJobInstance("BUSINESS_ERROR_KEEP", 6L, JobStatus.BUSINESS_ERROR);
    jobsCleanupExecutionScheduler.executeCleanupSucceededJobs();

    List<JobInstance> remainingJobs = jobRepository.findAll();
    Assert.assertEquals(remainingJobs.size(), 7);
    Assert.assertTrue(containsJobWithName(remainingJobs, "SUCCEEDED_KEEP"));
    Assert.assertTrue(containsJobWithName(remainingJobs, "OPEN_KEEP"));
    Assert.assertTrue(containsJobWithName(remainingJobs, "IN_PROGRESS_KEEP"));
    Assert.assertTrue(containsJobWithName(remainingJobs, "CANCELLED_KEEP"));
    Assert.assertTrue(containsJobWithName(remainingJobs, "ERRORNOUS_RETRIGGER_KEEP"));
    Assert.assertTrue(containsJobWithName(remainingJobs, "COMPLETED_ERRONEOUS_KEEP"));
    Assert.assertTrue(containsJobWithName(remainingJobs, "BUSINESS_ERROR_KEEP"));
  }

  private void createJobInstance(String name, Long lastExecutionPastMinutes, JobStatus status) {
    Instant lastExecutionDate = Instant.now().minus(lastExecutionPastMinutes, ChronoUnit.MINUTES);
    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(name);
    jobInstance.setPriority(0);
    jobInstance.setExecutions(3L);
    jobInstance.setStatus(status);
    jobInstance.setJobMap(new JobMap());
    jobInstance.setLastExecutionDate(lastExecutionDate);
    jobRepository.save(jobInstance);
  }

  private boolean containsJobWithName(List<JobInstance> remainingJobs, String jobName) {
    return remainingJobs.stream().anyMatch(job -> job.getJobName().equalsIgnoreCase(jobName));
  }
}
