package com.rodrigues.heric.incidentmanager.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateUsersRequest;
import com.rodrigues.heric.incidentmanager.dto.UsersDTO;
import com.rodrigues.heric.incidentmanager.exception.BusinessException;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.UsersMapper;
import com.rodrigues.heric.incidentmanager.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    @Transactional
    public UsersDTO getUserById(UUID id) {
        return this.usersRepository.findById(id)
                .map(usersMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " does not exist."));
    }

    @Transactional
    public UsersDTO createUser(CreateUsersRequest request) {
        this.usersRepository.findByEmail(request.email()).ifPresent(
                user -> {
                    throw new BusinessException("Email: " + request.email() + " already in use.");
                });

        UsersEntity userEntity = this.usersMapper.toEntity(request);
        UsersEntity savedUser = this.usersRepository.save(userEntity);
        return this.usersMapper.toDTO(savedUser);
    }

}
