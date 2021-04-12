package com.sss.scheduler.repository;

import com.sss.scheduler.domain.SimpleLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface SimpleLockRepository extends JpaRepository<SimpleLock, String> {

  void deleteAllByNameAndLockedBy(String name, String lockedBy);

  default boolean extendLockLifetime(String name, String lockedBy, Instant lockedUntil) {
    return updateLockLifetime(name, lockedBy, lockedUntil) == 1;
  }

  @Modifying
  @Query("UPDATE locks l SET l.lockedUntil = :lockedUntil WHERE l.name = :name AND l.lockedBy = :lockedBy")
  int updateLockLifetime(String name, String lockedBy, Instant lockedUntil);

  @Query("SELECT l FROM locks l WHERE l.name = :name AND l.lockedBy = :lockedBy AND l.lockedUntil = :locketAt")
  SimpleLock findActiveLock(@Param("name") String name, @Param("lockedBy") String lockedBy, @Param("locketAt") Instant locketAt);

  @Modifying
  @Transactional
  @Query("DELETE FROM locks l WHERE l.lockedUntil <= :now")
  int deleteOutdatedLocks(@Param("now") Instant now);

  SimpleLock findByNameAndLockedByAndLockedUntilIsBefore(String name, String lockedBy, Instant lockedUntil);
}
