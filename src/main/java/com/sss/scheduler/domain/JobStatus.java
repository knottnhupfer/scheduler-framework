package com.sss.scheduler.domain;

public enum JobStatus {
  OPEN,
  IN_PROGRESS,
  ERRORNOUS_RETRIGGER,
  COMPLETED_SUCCESSFUL,
  COMPLETED_ERRONEOUS,
  CANCELLED;
}
