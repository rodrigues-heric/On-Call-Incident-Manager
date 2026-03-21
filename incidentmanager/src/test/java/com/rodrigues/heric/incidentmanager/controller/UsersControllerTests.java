package com.rodrigues.heric.incidentmanager.controller;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rodrigues.heric.incidentmanager.dto.UsersDTO;
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

}
