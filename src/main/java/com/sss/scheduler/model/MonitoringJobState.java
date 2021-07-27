package com.sss.scheduler.model;

import com.sss.scheduler.domain.JobStatus;

import java.util.Arrays;
import java.util.List;

public enum MonitoringJobState {
  pending,
  failed,
  ;

  public static List<JobStatus> retrieveJobStates(MonitoringJobState status) {
    switch (status) {
      case failed:
        return Arrays.asList(JobStatus.BUSINESS_ERROR, JobStatus.COMPLETED_ERRONEOUS);
      case pending:
        return Arrays.asList(JobStatus.ERRORNOUS_RETRIGGER, JobStatus.OPEN, JobStatus.IN_PROGRESS);
      default:
        throw new IllegalStateException("Unknown status: " + status);
    }
  }
}
