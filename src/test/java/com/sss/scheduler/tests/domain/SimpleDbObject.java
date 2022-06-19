package com.sss.scheduler.tests.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "simple_db_object")
public class SimpleDbObject {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String value;
}
