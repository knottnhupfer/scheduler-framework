package com.sss.scheduler.domain;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.EnumType.STRING;

@Data
@Entity(name = "jobs")
public class JobInstance {

  static final String SEQ_ID = "cmd_seq_id";

  public JobInstance() {
    creationDate = Instant.now();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQ_ID)
  private Long id;

  @Column(nullable = false)
  private String jobName;

  @Column(nullable = false)
  private Long executions;

  @Column(nullable = false)
  private Integer priority;

  @Column(nullable = false)
  private Instant creationDate;

  @Column
  private String executeBy;

  @Column
  private Instant reservedUntil;

  @Column
  private Instant nextExecutionDate;

  @Column
  private Instant lastExecutionDate;

  @Column(nullable = false)
  @Type(type = "com.sss.scheduler.domain.JobMapType")
  private JobMap jobMap;

  @Enumerated(STRING)
  @Column(nullable = false)
  private JobStatus status;

  @Column
  private Long executionDuration;

  @Column
  private String executionResultMessage;

  @Column
  private Long businessObjectId;

  public String getShortDescription() {
    return String.format("Job(id:%d) '%s' with state:%s and next execution:%s", id, jobName, status, nextExecutionDate);
  }
}
