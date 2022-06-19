package com.sss.scheduler.tests.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleDbObjectRepository extends JpaRepository<SimpleDbObject, Long> {

  SimpleDbObject findByValue(String value);
}
