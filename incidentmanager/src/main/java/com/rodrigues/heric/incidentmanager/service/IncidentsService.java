package com.rodrigues.heric.incidentmanager.service;

import org.springframework.stereotype.Service;

import com.rodrigues.heric.incidentmanager.repository.IncidentsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentsService {

    private final IncidentsRepository incidentsRepository;

    @Transactional
    public void createIncident() {

    }

}
