package com.rodrigues.heric.incidentmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rodrigues.heric.incidentmanager.dto.CreateUsersRequest;
import com.rodrigues.heric.incidentmanager.dto.UsersDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.service.UsersService;

@WebMvcTest(UsersController.class)
@ActiveProfiles("test")
public class UsersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersService usersService;

    @Test
    @DisplayName("Should get user successfully")
    public void shouldReturUserById() throws Exception {
        UUID id = UUID.randomUUID();
        String email = "foo.bar@example.com";
        String name = "Foo Bar";
        String phone = "1234567890";
        UsersDTO response = new UsersDTO(id, name, email, phone);
        when(this.usersService.getUserById(id)).thenReturn(response);

        this.mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value(response.email()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.phone").value(response.phone()));
    }

    @Test
    @DisplayName("Should return Resource Not Found Exception when getting user")
    public void whenUserDoesNotExist_thenItShouldReturnResourceNotFoundException() throws Exception {
        UUID id = UUID.randomUUID();

        when(this.usersService.getUserById(id))
                .thenThrow(new ResourceNotFoundException("User with ID: " + id + " does not exist."));

        this.mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create user successfully")
    public void shouldCreateUser() throws Exception {
        String email = "foo.bar@example.com";
        String name = "Foo Bar";
        String phone = "1234567890";
        String jsonStr = """
                {
                    "name": "%s",
                    "email": "%s",
                    "phone": "%s"
                }
                """.formatted(name, email, phone);
        UUID id = UUID.randomUUID();
        UsersDTO response = new UsersDTO(id, name, email, phone);

        when(this.usersService.createUser(any(CreateUsersRequest.class))).thenReturn(response);

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.email").value(response.email()))
                .andExpect(jsonPath("$.phone").value(response.phone()));
    }

}
