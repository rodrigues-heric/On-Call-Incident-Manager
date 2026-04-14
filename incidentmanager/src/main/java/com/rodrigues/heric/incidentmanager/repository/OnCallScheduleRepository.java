package com.rodrigues.heric.incidentmanager.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.OnCallScheduleEntity;

@Repository
public interface OnCallScheduleRepository extends JpaRepository<OnCallScheduleEntity, Long> {

    Optional<OnCallScheduleEntity> findFirstByServiecIdAndStartTimeBeforeAndEndTimeAfter(
            UUID serviceId,
            LocalDateTime startTime,
            LocalDateTime endTime);

}
