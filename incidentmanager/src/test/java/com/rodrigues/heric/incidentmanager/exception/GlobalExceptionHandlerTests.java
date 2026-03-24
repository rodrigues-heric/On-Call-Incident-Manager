package com.rodrigues.heric.incidentmanager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.rodrigues.heric.incidentmanager.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 422 Unprocessable Content when throwing InvalidStateTransitionException")
    void shouldReturnUnprocessableContentWhenInvalidStateTransition() {
        String errorMessage = "Invalid transition from \"OPEN\" to \"RESOLVED\"";
        InvalidStateTransitionException exception = new InvalidStateTransitionException(errorMessage);
        HttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<ErrorResponseDTO> response = handler.handleInvalidStateTransitionException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid State Transition", response.getBody().error());
        assertEquals(errorMessage, response.getBody().message());
        assertNotNull(response.getBody().timestamp());
        assertNotNull(response.getBody().path());
    }

}
