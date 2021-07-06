package com.sss.scheduler.execution;

import com.sss.scheduler.config.SchedulerConfiguration;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class JobsCleanupExecutionScheduler extends AbstractLockedScheduler {

  @Resource
  private SchedulerConfiguration schedulerConfiguration;

  @Resource
  private JobRepository jobRepository;

  protected JobsCleanupExecutionScheduler() {
    super(JobsCleanupExecutionScheduler.class.getName(), 15L);
  }

  @Scheduled(fixedRateString = "${scheduler.job-execution.jobs-age-cleanup-interval}")
  void executeCleanupSucceededJobs() {
    executeWithLock();
  }

  @Override
  protected void execute0() {
    jobRepository.deleteSuccessfullyTerminatedJobsOlderThen(schedulerConfiguration.getJobsAgeCleanupMinutes());
    log.info("Successfully executed cleanup succeeded jobs scheduler.");
  }
}
