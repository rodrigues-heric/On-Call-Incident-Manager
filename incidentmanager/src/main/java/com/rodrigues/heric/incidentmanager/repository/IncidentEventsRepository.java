package com.rodrigues.heric.incidentmanager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.IncidentEventsEntity;

@Repository
public interface IncidentEventsRepository extends JpaRepository<IncidentEventsEntity, Long> {

    List<IncidentEventsEntity> findByIncidentIdOrderByCreatedAtDesc(UUID incidentId);

}
