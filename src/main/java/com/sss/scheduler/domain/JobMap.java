package com.sss.scheduler.domain;

import lombok.Data;
import org.springframework.util.Assert;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

@Data
public class JobMap implements ExecutionMap {

  private Map<String, String> mappings = new HashMap<>();

  @Transient
  public String getStringValue(String key) {
    return mappings.get(key);
  }

  public Long loadLongValue(String key) {
    String value = mappings.get(key);
    if(value == null) {
      throw new IllegalStateException(String.format("No long value found for key: {}", key));
    } else {
      return Long.valueOf(value);
    }
  }

  @Transient
  public Long getLongValue(String key) {
    String value = mappings.get(key);
    if(value == null) {
      return null;
    } else {
      return Long.valueOf(value);
    }
  }

  @Transient
  public Long getLongValue(String key, Long alternative) {
    Long longValue = getLongValue(key);
    if(longValue == null) {
      longValue = alternative;
    }
    return longValue;
  }

  public void putValue(String key, String value) {
    Assert.hasText(key, "value is not set");
    Assert.hasText(value, "value is not set");
    mappings.put(key, value);
  }

  public void putLongValue(String key, Long value) {
    Assert.hasText(key, "value is not set");
    Assert.notNull(value, "value is not set");
    mappings.put(key, value.toString());
  }
}
