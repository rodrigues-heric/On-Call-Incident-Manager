package com.rodrigues.heric.incidentmanager.domain.state;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.exception.InvalidStateTransitionException;

@Component
public class IncidentStateMachine {

    private final Map<IncidentStatusEnum, Set<IncidentStatusEnum>> transitionsMap = new EnumMap<IncidentStatusEnum, Set<IncidentStatusEnum>>(
            IncidentStatusEnum.class);

    public IncidentStateMachine() {
        transitionsMap.put(IncidentStatusEnum.OPEN, Set.of(IncidentStatusEnum.ACKNOWLEDGED));
        transitionsMap.put(IncidentStatusEnum.ACKNOWLEDGED,
                Set.of(IncidentStatusEnum.INVESTIGATING, IncidentStatusEnum.ESCALATED));
        transitionsMap.put(IncidentStatusEnum.INVESTIGATING, Set.of(IncidentStatusEnum.MITIGATING));
        transitionsMap.put(IncidentStatusEnum.MITIGATING, Set.of(IncidentStatusEnum.RESOLVED));
        transitionsMap.put(IncidentStatusEnum.ESCALATED, Set.of(IncidentStatusEnum.ACKNOWLEDGED));
    }

    public void validateTransition(IncidentStatusEnum currentStatus, IncidentStatusEnum nextStatus) {
        Set<IncidentStatusEnum> validNextStates = transitionsMap.getOrDefault(currentStatus, Set.of());

        if (!validNextStates.contains(nextStatus)) {
            throw new InvalidStateTransitionException("""
                    Invalid transition: it is not possible to change from "%s" to "%s"
                    """.formatted(currentStatus.toString(), nextStatus.toString()));
        }
    }

}
