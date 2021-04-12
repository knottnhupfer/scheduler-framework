package com.sss.scheduler.domain;

public enum CommandStatus {
  OPEN,
  IN_PROGRESS,
  COMPLETED_SUCCESSFUL,
  COMPLETED_ERRONEOUS,
  CANCELLED;
}
