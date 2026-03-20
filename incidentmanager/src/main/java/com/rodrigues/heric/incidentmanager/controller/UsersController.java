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

import com.rodrigues.heric.incidentmanager.dto.CreateUsersRequest;
import com.rodrigues.heric.incidentmanager.dto.UsersDTO;
import com.rodrigues.heric.incidentmanager.service.UsersService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/{id}")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable UUID id) {
        UsersDTO userDTO = usersService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping()
    public ResponseEntity<UsersDTO> createUser(@RequestBody @Valid CreateUsersRequest request) {
        UsersDTO userDTO = usersService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

}
