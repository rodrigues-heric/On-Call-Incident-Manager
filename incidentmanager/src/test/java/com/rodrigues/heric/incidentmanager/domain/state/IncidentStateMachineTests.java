package com.rodrigues.heric.incidentmanager.domain.state;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.exception.InvalidStateTransitionException;

public class IncidentStateMachineTests {

    private final IncidentStateMachine stateMachine = new IncidentStateMachine();

    @ParameterizedTest(name = "Should allow transition from {0} to {1}")
    @MethodSource("provideValidTransitions")
    @DisplayName("Allowed transitions")
    public void shouldAllowValidTransitions(IncidentStatusEnum current, IncidentStatusEnum next) {
        assertDoesNotThrow(() -> stateMachine.validateTransition(current, next));
    }

    @ParameterizedTest(name = "Should not allow transition from {0} to {1}")
    @MethodSource("provideInvalidTransitions")
    @DisplayName("Not allowed transitions")
    public void shouldNotAllowInvalidTransitions(IncidentStatusEnum current, IncidentStatusEnum next) {
        assertThrows(InvalidStateTransitionException.class, () -> {
            stateMachine.validateTransition(current, next);
        });
    }

    @Test
    @DisplayName("Should launch detailed error message")
    public void shouldReturnDetailedErrorMessage() {
        InvalidStateTransitionException exception = assertThrows(InvalidStateTransitionException.class, () -> {
            stateMachine.validateTransition(IncidentStatusEnum.OPEN, IncidentStatusEnum.RESOLVED);
        });

        assertTrue(exception.getMessage().contains("Invalid transition"));
        assertTrue(exception.getMessage().contains("OPEN"));
        assertTrue(exception.getMessage().contains("RESOLVED"));
    }

    private static Stream<Arguments> provideValidTransitions() {
        return Stream.of(
                Arguments.of(IncidentStatusEnum.OPEN, IncidentStatusEnum.ACKNOWLEDGED),
                Arguments.of(IncidentStatusEnum.ACKNOWLEDGED, IncidentStatusEnum.INVESTIGATING),
                Arguments.of(IncidentStatusEnum.ACKNOWLEDGED, IncidentStatusEnum.ESCALATED),
                Arguments.of(IncidentStatusEnum.INVESTIGATING, IncidentStatusEnum.MITIGATING),
                Arguments.of(IncidentStatusEnum.MITIGATING, IncidentStatusEnum.RESOLVED),
                Arguments.of(IncidentStatusEnum.ESCALATED, IncidentStatusEnum.ACKNOWLEDGED));
    }

    private static Stream<Arguments> provideInvalidTransitions() {
        return Stream.of(
                Arguments.of(IncidentStatusEnum.OPEN, IncidentStatusEnum.RESOLVED),
                Arguments.of(IncidentStatusEnum.OPEN, IncidentStatusEnum.INVESTIGATING),
                Arguments.of(IncidentStatusEnum.RESOLVED, IncidentStatusEnum.OPEN),
                Arguments.of(IncidentStatusEnum.MITIGATING, IncidentStatusEnum.OPEN),
                Arguments.of(IncidentStatusEnum.INVESTIGATING, IncidentStatusEnum.OPEN));
    }
}
