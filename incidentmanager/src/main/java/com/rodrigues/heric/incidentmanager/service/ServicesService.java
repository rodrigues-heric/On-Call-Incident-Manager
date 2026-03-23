package com.rodrigues.heric.incidentmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateServicesRequest;
import com.rodrigues.heric.incidentmanager.dto.ServicesDTO;
import com.rodrigues.heric.incidentmanager.exception.BusinessException;
import com.rodrigues.heric.incidentmanager.mapper.ServicesMapper;
import com.rodrigues.heric.incidentmanager.repository.ServicesRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicesService {

    private final ServicesRepository servicesRepository;
    private final ServicesMapper servicesMapper;

    @Transactional
    public ServicesDTO createService(CreateServicesRequest request) {
        this.servicesRepository.findByName(request.name()).ifPresent(
                service -> {
                    throw new BusinessException("Service " + request.name() + " already in use");
                });

        ServicesEntity serviceEntity = this.servicesMapper.toEntity(request);
        ServicesEntity savedService = this.servicesRepository.save(serviceEntity);
        return this.servicesMapper.toDTO(savedService);
    }

    @Transactional
    public List<ServicesDTO> listAllServices() {
        return this.servicesRepository.findAll()
                .stream()
                .map(this.servicesMapper::toDTO)
                .toList();
    }

}
