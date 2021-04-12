package com.sss.scheduler.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Entity(name = "locks")
public class SimpleLock {

  @Id
  private String name;
  private Instant lockedUntil;
  private Instant lockedAt;
  private String lockedBy;
}
