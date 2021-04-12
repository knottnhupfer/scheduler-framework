package com.sss.scheduler.domain;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;

import static javax.persistence.EnumType.STRING;

@Data
@Entity(name = "commands")
public class Command {

  static final String SEQ_ID = "cmd_seq_id";

  public Command() {
    creationDate = OffsetDateTime.now();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQ_ID)
  private Long id;

  @Column(nullable = false)
  private String commandName;

  @Column(nullable = false)
  private Integer priority;

  @Column(nullable = false)
  private OffsetDateTime creationDate;

  @Column(nullable = false)
  private OffsetDateTime nextExecutionDate;

  private OffsetDateTime lastExecutionDate;

  @Column(nullable = false)
  @Type(type = "com.sss.scheduler.domain.CommandMapType")
  private CommandMap commandMap;

  @Enumerated(STRING)
  @Column(nullable = false)
  private CommandStatus status;

  private Long executionDuration;

  private String executionResultMessage;
}
