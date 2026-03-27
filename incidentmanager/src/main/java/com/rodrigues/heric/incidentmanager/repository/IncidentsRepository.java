package com.rodrigues.heric.incidentmanager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;

@Repository
public interface IncidentsRepository extends JpaRepository<IncidentsEntity, UUID> {

    @EntityGraph(attributePaths = { "service", "assignee" })
    Optional<IncidentsEntity> findById(UUID id);

}
