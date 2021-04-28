package com.sss.scheduler.domain;

public enum JobStatus {
  OPEN(false),
  IN_PROGRESS(false),
  ERRORNOUS_RETRIGGER(false),
  COMPLETED_SUCCESSFUL(true),
  COMPLETED_ERRONEOUS(true),
  CANCELLED(true);

  private boolean finalStatus;

  JobStatus(boolean finalStatus) {
    this.finalStatus = finalStatus;
  }

  public boolean isFinal() {
    return finalStatus;
  }
}
