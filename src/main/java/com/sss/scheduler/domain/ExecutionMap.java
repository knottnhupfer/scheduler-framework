package com.sss.scheduler.domain;

public interface ExecutionMap {

  Long getLongValue(String key);

  Long getLongValue(String key, Long alternative);

  String getStringValue(String key);
}
