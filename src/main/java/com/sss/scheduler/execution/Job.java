package com.sss.scheduler.execution;

import com.sss.scheduler.domain.ExecutionMap;

public interface Job {

  void execute(ExecutionMap map);
}
