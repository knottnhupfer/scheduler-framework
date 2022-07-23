package com.sss.scheduler.service;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.tests.JobTestService;
import com.sss.scheduler.utils.TestComparisonUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.UUID;

@SpringBootTest
class JobInstancePersistenceTests {

  @Resource
  private JobService jobService;

  @Resource
  private JobTestService jobTestService;

	@Test
	void storeSuccessfullyJob() {
    String jobName = "testJob-" + UUID.randomUUID().toString().substring(0, 4);
    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(jobName);
    jobInstance.setPriority(10);
    jobInstance.setBusinessObjectId(123L);
    jobInstance.setExecutions(0L);

    JobMap jobMap = new JobMap();
    jobMap.putValue("jobName", jobName);
    jobInstance.setJobMap(jobMap);
    jobService.createJob(jobInstance);

    JobInstance fetchedJobInstance = jobTestService.getJobByName(jobName);

    Assert.assertNotNull(fetchedJobInstance.getId());
    Assert.assertEquals(fetchedJobInstance.getStatus(), JobStatus.OPEN);
    Assert.assertEquals(fetchedJobInstance.getJobName(), jobName);
    Assert.assertEquals(fetchedJobInstance.getBusinessObjectId().longValue(), 123L);
    Assert.assertEquals(fetchedJobInstance.getPriority().longValue(), 10L);
    Assert.assertEquals(fetchedJobInstance.getJobMap().getStringValue("jobName"), jobName);
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getCreationDate());
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ZERO);
  }

  @Test
  void storeJobAndLoadJobConfiguration() {
    String jobName = "ui-notification";
    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(jobName);
    jobInstance.setExecutions(0L);
    jobService.createJob(jobInstance);

    JobInstance fetchedJobInstance = jobTestService.getJobByName(jobName);
    Assert.assertEquals(fetchedJobInstance.getPriority().longValue(), 100L);
    TestComparisonUtil.assertDateCloseToNow(fetchedJobInstance.getNextExecutionDate(), Duration.ofSeconds(0));
  }

  @Test
  void storeJobAndUpdatePriority() {
    String jobName = "testJob-" + UUID.randomUUID().toString().substring(0, 4);
    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(jobName);
    jobInstance.setExecutions(0L);
    jobInstance.setPriority(1207);
    jobService.createJob(jobInstance);

    JobInstance fetchedJobInstance = jobTestService.getJobByName(jobName);
    Assert.assertEquals(fetchedJobInstance.getPriority().longValue(), 1207L);
  }
}
