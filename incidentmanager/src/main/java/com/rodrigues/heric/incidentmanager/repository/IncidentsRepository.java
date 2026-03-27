package com.rodrigues.heric.incidentmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;

@Repository
public interface IncidentsRepository extends JpaRepository<IncidentsEntity, UUID> {

}
