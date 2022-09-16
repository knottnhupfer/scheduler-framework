package com.sss.scheduler.execution;

import com.sss.scheduler.config.SchedulerConfiguration;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobsCleanupExecutionScheduler extends AbstractLockedScheduler {

  private final JobRepository jobRepository;

  private final SchedulerConfiguration schedulerConfiguration;

  public JobsCleanupExecutionScheduler(JobRepository jobRepository, SchedulerConfiguration schedulerConfiguration) {
    super(JobsCleanupExecutionScheduler.class.getName(), 15L);
    this.jobRepository = jobRepository;
    this.schedulerConfiguration = schedulerConfiguration;
  }

  @Scheduled(fixedRateString = "${scheduler.job-execution.jobs-age-cleanup-interval:1800000}")
  void executeCleanupSucceededJobs() {
    executeWithLock();
  }

  @Override
  protected void execute0() {
    jobRepository.deleteSuccessfullyTerminatedJobsOlderThen(schedulerConfiguration.getJobsAgeCleanupMinutes());
    log.debug("Successfully executed cleanup succeeded jobs scheduler.");
  }
}
