package com.sss.scheduler.repository;

import com.sss.scheduler.domain.SimpleLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface SimpleLockRepository extends JpaRepository<SimpleLock, String> {

  void deleteAllByNameAndLockedBy(String name, String lockedBy);

  @Modifying
  @Query("UPDATE locks l SET l.lockedUntil = :lockedUntil WHERE l.name = :name AND l.lockedBy = :lockedBy")
  int update(String name, String lockedBy, Instant lockedUntil);

  @Query("SELECT l FROM locks l WHERE l.name = :name AND l.lockedBy = :lockedBy AND l.lockedUntil = :locketAt")
  SimpleLock findActiveLock(@Param("name") String name, @Param("lockedBy") String lockedBy, @Param("locketAt") Instant locketAt);

  @Modifying
  @Query("DELETE FROM locks l WHERE l.lockedUntil <= :now")
  void deleteOutdatedLocks(@Param("now") Instant now);
}
