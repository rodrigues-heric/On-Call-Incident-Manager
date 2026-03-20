package com.rodrigues.heric.incidentmanager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, UUID> {

    Optional<UsersEntity> findByEmail(String email);

}
