package com.sss.scheduler.components;

public abstract class AbstractRetryStrategy implements RetryStrategy {

  private String strategyName;

  protected AbstractRetryStrategy(String strategyName) {
    this.strategyName = strategyName;
  }

  public String getStrategyName() {
    return strategyName;
  }
}
