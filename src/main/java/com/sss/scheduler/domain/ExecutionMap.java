package com.sss.scheduler.domain;

import java.time.Instant;

public interface ExecutionMap {

  Long getLongValue(String key);

  Long getLongValue(String key, Long alternative);

  String getStringValue(String key);

  Instant getInstantValue(String key);

  String loadStringValue(String key);

  Long loadLongValue(String key);

  Instant loadInstantValue(String key);
}
