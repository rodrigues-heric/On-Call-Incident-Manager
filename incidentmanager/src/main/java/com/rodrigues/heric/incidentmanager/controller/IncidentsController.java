package com.rodrigues.heric.incidentmanager.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.service.IncidentsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentsController {

    private final IncidentsService incidentsService;

    @GetMapping("/{id}")
    public ResponseEntity<IncidentsDTO> getIncidentById(@PathVariable UUID id) {
        IncidentsDTO incidentDTO = this.incidentsService.getIncidentById(id);
        return ResponseEntity.ok(incidentDTO);
    }

    @PostMapping()
    public ResponseEntity<IncidentsDTO> postIncident(@RequestBody @Valid CreateIncidentsRequest request) {
        IncidentsDTO incidentDTO = this.incidentsService.createIncident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentDTO);
    }

}
