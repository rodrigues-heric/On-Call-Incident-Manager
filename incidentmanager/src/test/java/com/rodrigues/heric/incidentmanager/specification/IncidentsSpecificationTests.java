package com.rodrigues.heric.incidentmanager.specification;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentsSpecificationTests {

    @Mock
    private Root<IncidentsEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Expression<String> lowerExpression;

    @Mock
    private Path<String> titlePath;

    @Mock
    private Path<Object> path;

    @Mock
    private Path<Object> joinPath;

    @BeforeEach
    void setUp() {
        lenient().when(root.get(anyString())).thenReturn(path);
    }

    @Test
    @DisplayName("Should create an equality predicate for status when informed")
    void statusEquals_ShouldReturnEqualPredicate() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.statusEquals(IncidentStatusEnum.OPEN);
        spec.toPredicate(root, query, cb);

        verify(root).get("status");
        verify(cb).equal(path, IncidentStatusEnum.OPEN);
    }

    @Test
    @DisplayName("It should return conjunction when status is null")
    void statusEquals_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.statusEquals(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
        verify(cb, never()).equal(any(), any());
    }

    @Test
    @DisplayName("Should create a predicate for serviceId by accessing a nested field")
    void serviceIdEquals_ShouldReturnNestedPathEqualPredicate() {
        UUID serviceId = UUID.randomUUID();
        when(root.get("service")).thenReturn(joinPath);
        when(joinPath.get("id")).thenReturn(path);

        Specification<IncidentsEntity> spec = IncidentsSpecification.serviceIdEquals(serviceId);
        spec.toPredicate(root, query, cb);

        verify(cb).equal(path, serviceId);
    }

    @Test
    @DisplayName("Should create a lowercase LIKE predicate for the title.")
    void titleContains_ShouldReturnLikePredicate() {
        String title = "DATABASE";

        when(root.<String>get("title")).thenReturn(titlePath);
        when(cb.lower(titlePath)).thenReturn(lowerExpression);

        Specification<IncidentsEntity> spec = IncidentsSpecification.titleContains(title);
        spec.toPredicate(root, query, cb);

        verify(cb).like(lowerExpression, "%database%");
    }

    @Test
    @DisplayName("Should create an isNull predicate for unresolved incidents.")
    void isNotResolved_ShouldReturnIsNullPredicate() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.isNotResolved();
        spec.toPredicate(root, query, cb);

        verify(root).get("resolvedAt");
        verify(cb).isNull(path);
    }

    @Test
    @DisplayName("Should create a date predicate that is greater than or equal to the date for createdAt.")
    void createdAtAfter_ShouldReturnGreaterThanOrEqualTo() {
        LocalDateTime now = LocalDateTime.now();
        Specification<IncidentsEntity> spec = IncidentsSpecification.createdAtAfter(now);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(root.get("createdAt"), now);
    }

    @Test
    @DisplayName("Should combine multiple filters correctly.")
    void withFilters_ShouldCombineAllSpecs() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.withFilters(
                IncidentStatusEnum.ACKNOWLEDGED,
                null,
                CriticalityEnum.HIGH,
                null,
                "Latency");

        spec.toPredicate(root, query, cb);

        verify(root).get("status");
        verify(root).get("criticality");
        verify(root).get("title");
    }
}