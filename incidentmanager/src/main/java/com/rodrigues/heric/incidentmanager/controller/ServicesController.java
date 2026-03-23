package com.rodrigues.heric.incidentmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigues.heric.incidentmanager.dto.ServicesDTO;
import com.rodrigues.heric.incidentmanager.service.ServicesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServicesController {

    private final ServicesService servicesService;

    @GetMapping("/all")
    public ResponseEntity<List<ServicesDTO>> getAllServices() {
        List<ServicesDTO> result = this.servicesService.listAllServices();
        return ResponseEntity.ok(result);
    }

}
