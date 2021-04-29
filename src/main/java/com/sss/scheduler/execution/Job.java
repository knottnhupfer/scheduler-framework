package com.sss.scheduler.execution;

import com.sss.scheduler.domain.ExecutionMap;

public interface Job {

  default String getName() {
    return this.getClass().getSimpleName().substring(0, 1).toLowerCase() + this.getClass().getSimpleName().substring(1);
  }

  void execute(ExecutionMap map);
}
