package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.Assert.fail;

@SpringBootTest
public class JobsExecutorServiceTests extends AbstractJobsExecutionTest {

  @Resource
  private JobsExecuter jobsExecuter;

  @Test
  public void noJobFound() {
    try {
      jobsExecuter.executeJob("not-valid", 1L, new JobMap());
      fail();
    } catch (Exception e) {
      Assert.assertEquals("Unable to load job with name 'not-valid'.", e.getMessage());
    }
  }

  @Test
  public void executeSuccessfulJob() {
    try {
      jobsExecuter.executeJob("successfulJob", 1L, new JobMap());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void executeFailingJob() {
    try {
      jobsExecuter.executeJob("failingJob", 1L, new JobMap());
      fail();
    } catch (Exception e) {
      Assert.assertTrue("validate correct exception", e.getMessage().contains("Execute failing job."));
    }
  }

  @Test
  public void executeBusinessErrorJob() {
    jobRepository.deleteAll();
    createNewJob("businessErrorJob", "dummyCounter");
    assignCreatedJob("businessErrorJob");
    jobsExecutionScheduler.executeAssignedJobs();

    try {
      jobsExecuter.executeJob("businessErrorJob", 1L, new JobMap());
      fail();
    } catch (Exception e) {
      Assert.assertEquals("Business error happened.", e.getMessage());
    }
  }
}
