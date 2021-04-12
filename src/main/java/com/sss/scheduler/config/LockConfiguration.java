package com.sss.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "scheduler.lock")
public class LockConfiguration {

  private Long retries = 4L;
  private Long intervalMs = 750L;
}
