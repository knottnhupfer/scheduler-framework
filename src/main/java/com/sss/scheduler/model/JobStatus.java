package com.sss.scheduler.model;

import java.util.Arrays;
import java.util.List;

public enum JobStatus {
  pending,
  failed,
  ;

  public static List<com.sss.scheduler.domain.JobStatus> convertStatus(JobStatus status) {
    switch (status) {
      case failed:
        return Arrays.asList(
                com.sss.scheduler.domain.JobStatus.BUSINESS_ERROR,
                com.sss.scheduler.domain.JobStatus.COMPLETED_ERRONEOUS);
      case pending:
        return Arrays.asList(
                com.sss.scheduler.domain.JobStatus.ERRORNOUS_RETRIGGER,
                com.sss.scheduler.domain.JobStatus.OPEN,
                com.sss.scheduler.domain.JobStatus.IN_PROGRESS);
      default:
        throw new IllegalStateException("Unknown status: " + status);
    }
  }
}
