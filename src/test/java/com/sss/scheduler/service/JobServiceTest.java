package com.sss.scheduler.service;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;

import static org.junit.Assert.assertTrue;

@SpringBootTest
public class JobServiceTest {

  @Resource
  private JobService jobService;

  @Resource
  private JobRepository jobRepository;

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
}
