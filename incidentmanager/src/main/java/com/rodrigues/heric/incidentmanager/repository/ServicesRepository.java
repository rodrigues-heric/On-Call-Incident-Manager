package com.rodrigues.heric.incidentmanager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;

@Repository
public interface ServicesRepository extends JpaRepository<ServicesEntity, UUID> {

    public Optional<ServicesEntity> findByName(String name);

}
