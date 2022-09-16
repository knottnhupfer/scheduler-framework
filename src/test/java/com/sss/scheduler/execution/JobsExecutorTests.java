package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.tests.BusinessErrorJob;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.Assert.fail;

@SpringBootTest
public class JobsExecutorTests extends AbstractJobsExecutionTest {

  @Resource
  private JobsExecutor jobsExecutor;

//  @Resource
//  private JobsExecutionScheduler jobsExecutionScheduler;
//
//  @Resource
//  private JobsAssignmentScheduler jobsAssignmentScheduler;

  @Test
  public void noJobFound() {
    try {
      jobsExecutor.executeJob("not-valid", 1L, new JobMap());
      fail();
    } catch (Exception e) {
      Assert.assertEquals("Unable to load job with name 'not-valid'.", e.getMessage());
    }
  }

  @Test
  public void executeSuccessfulJob() {
    try {
      jobsExecutor.executeJob("successfulJob", 1L, new JobMap());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void executeFailingJob() {
    try {
      jobsExecutor.executeJob("failingJob", 1L, new JobMap());
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
      jobsExecutor.executeJob("businessErrorJob", 1L, new JobMap());
      fail();
    } catch (Exception e) {
      Assert.assertEquals("Business error happened.", e.getMessage());
    }
  }
}
