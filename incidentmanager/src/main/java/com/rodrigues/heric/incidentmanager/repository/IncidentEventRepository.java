package com.rodrigues.heric.incidentmanager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.IncidentEventEntity;

@Repository
public interface IncidentEventRepository extends JpaRepository<IncidentEventEntity, Long> {

    List<IncidentEventEntity> findByIncidentIdOrderByTimestampDesc(UUID incidentId);

}
