package com.sss.scheduler.execution;

import com.sss.scheduler.config.SchedulerConfiguration;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Slf4j
@Service
public class JobsAssignmentScheduler {

  private static final String ASSIGN_JOBS = "assignJobs";

  private boolean applicationActive = false;

  @Resource
  private JobService jobService;

  @Resource
  private LockManager lockManager;

  @Resource
  private SchedulerConfiguration schedulerConfiguration;

  @EventListener(classes = { ContextStartedEvent.class })
  public void enableExecuter() {
    applicationActive = schedulerConfiguration.isEnabled();
  }

  @Scheduled(fixedRateString = "${scheduler.job-execution.jobs-assigning-interval:15000}")
  void assignJobsToExecute() {
    if(!applicationActive) {
      return;
    }

    try {
      boolean lock = lockManager.lock(ASSIGN_JOBS, Duration.ofSeconds(45));
      if(!lock) {
        log.error("Unable to retrieve lock: {}", ASSIGN_JOBS);
        return;
      }
      jobService.retrieveAndAssignJobs();
    } finally {
      lockManager.unlock(ASSIGN_JOBS);
    }
  }
}
