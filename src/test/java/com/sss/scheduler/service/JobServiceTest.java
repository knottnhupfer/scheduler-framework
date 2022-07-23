package com.sss.scheduler.service;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.execution.TestJobCreator;
import com.sss.scheduler.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

@SpringBootTest
public class JobServiceTest {

  @Resource
  private JobService jobService;

  @Resource
  private JobRepository jobRepository;

  @Resource
  private TestJobCreator testJobCreator;

  @Test
  void storeUpdateNextExecutionDateForJob() {
    jobRepository.deleteAll();
    Long businessObjectId = 456L;
    String jobName = "nextExecutionDateTestJob";
    JobInstance jobInstance = new JobInstance();
    jobInstance.setBusinessObjectId(businessObjectId);
    jobInstance.setJobName(jobName);
    jobService.createJob(jobInstance);

    JobInstance storedJob = jobRepository.findAllJobsByName(jobName).get(0);
    assertTrue(storedJob.getNextExecutionDate().isBefore(Instant.now()));

    jobService.updateNextExecutionDate(jobName, businessObjectId, Instant.now().plusSeconds(60));
    storedJob = jobRepository.findAllJobsByName(jobName).get(0);
    assertTrue(storedJob.getNextExecutionDate().isAfter(Instant.now()));
  }

  @Test
  void processedJobExternalManually() {
    long uniqueId = new Random().nextLong();
    String jobName = "UpdateJobStatus-" + uniqueId;
    testJobCreator.createJob(jobName, uniqueId);
    List<JobInstance> jobInstances = jobService.loadJobsByStatus(jobName, JobStatus.OPEN);
    assertEquals("fetched job amounts", 1, jobInstances.size());

    jobService.updateJobByBusinessObjectIdToStatusCompletedExternally(jobName, uniqueId);
    jobInstances = jobService.loadJobsByStatus(jobName, JobStatus.COMPLETED_SOLVED_EXTERNALLY);
    assertEquals("fetched job amounts", 1, jobInstances.size());
  }

  @Test
  void processedJobExternalManuallyFails() {
    long uniqueId = new Random().nextLong();
    String jobName = "UpdateJobStatus-" + uniqueId;
    testJobCreator.createJob(jobName, uniqueId);
    testJobCreator.createJob(jobName, uniqueId);

    List<JobInstance> jobInstances = jobService.loadJobsByStatus(jobName, JobStatus.OPEN);
    assertEquals("fetched job amounts", 2, jobInstances.size());
    try {
      jobService.updateJobByBusinessObjectIdToStatusCompletedExternally(jobName, uniqueId);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("error message", "Found 2 jobs, expecting exactly a single job.", e.getMessage());
    }
  }
}
