package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobsExecutionScheduler {

  private final LockManager lockManager;

  private final JobsExecutor jobsExecutor;

  private final JobRepository jobRepository;

  @Scheduled(fixedRateString = "${scheduler.job-execution.jobs-execution-interval:15000}")
  void executeAssignedJobs() {
    List<JobInstance> assignedJobs = jobRepository.findAssignedjobs(lockManager.getDefaultLockName());
    for(JobInstance job : assignedJobs) {
      log.debug("Execute job:{} with id:{}", job.getId(), job.getJobName());
      jobsExecutor.executeJob(job.getId());
    }
  }
}
