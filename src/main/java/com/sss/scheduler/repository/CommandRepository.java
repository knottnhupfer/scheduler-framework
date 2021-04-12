package com.sss.scheduler.repository;

import com.sss.scheduler.domain.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommandRepository extends JpaRepository<Command, Long> {

  @Query("SELECT c FROM commands c WHERE c.commandName = :commandName ORDER BY c.creationDate DESC")
  List<Command> findAllCommandsByName(@Param("commandName") String commandName);
}
