package com.sss.scheduler.execution;

import com.sss.scheduler.domain.ExecutionMap;

public interface Job {

  default String getName() {
    return this.getClass().getSimpleName().substring(0, 1).toLowerCase() + this.getClass().getSimpleName().substring(1);
  }

  default void execute(ExecutionMap map) {
    throw new IllegalStateException("'execute(ExecutionMap map)' not implemented yet!");
  }

  default void execute(Long businessObjectId, Long previousExecutions, ExecutionMap map) {
    execute(businessObjectId, map);
  }

  default void execute(Long businessObjectId, ExecutionMap map) {
    execute(map);
  }
}
