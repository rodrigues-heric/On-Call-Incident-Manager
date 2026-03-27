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

    @Test
    @DisplayName("Should create an equality predicate for criticality when informed")
    void criticalityEquals_ShouldReturnEqualPredicate() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.criticalityEquals(CriticalityEnum.HIGH);
        spec.toPredicate(root, query, cb);

        verify(root).get("criticality");
        verify(cb).equal(path, CriticalityEnum.HIGH);
    }

    @Test
    @DisplayName("Should return conjunction when criticality is null")
    void criticalityEquals_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.criticalityEquals(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Should create a predicate for assigneeId by accessing a nested field")
    void assigneeIdEquals_ShouldReturnNestedPathEqualPredicate() {
        UUID assigneeId = UUID.randomUUID();
        when(root.get("assignee")).thenReturn(joinPath);
        when(joinPath.get("id")).thenReturn(path);

        Specification<IncidentsEntity> spec = IncidentsSpecification.assigneeIdEquals(assigneeId);
        spec.toPredicate(root, query, cb);

        verify(cb).equal(path, assigneeId);
    }

    @Test
    @DisplayName("Should return conjunction when assigneeId is null")
    void assigneeIdEquals_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.assigneeIdEquals(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Should return conjunction when title is blank or null")
    void titleContains_ShouldReturnConjunction_WhenBlankOrNull() {
        Specification<IncidentsEntity> specNull = IncidentsSpecification.titleContains(null);
        specNull.toPredicate(root, query, cb);

        Specification<IncidentsEntity> specBlank = IncidentsSpecification.titleContains("   ");
        specBlank.toPredicate(root, query, cb);

        verify(cb, times(2)).conjunction();
    }

    @Test
    @DisplayName("Should return conjunction when date is null in createdAtAfter")
    void createdAtAfter_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.createdAtAfter(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Should create a lessThanOrEqualTo predicate for createdAtBefore")
    void createdAtBefore_ShouldReturnLessThanOrEqualTo() {
        LocalDateTime date = LocalDateTime.now();
        Specification<IncidentsEntity> spec = IncidentsSpecification.createdAtBefore(date);
        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(root.get("createdAt"), date);
    }

    @Test
    @DisplayName("Should return conjunction when date is null in createdAtBefore")
    void createdAtBefore_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.createdAtBefore(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Should create a greaterThanOrEqualTo predicate for resolvedAtAfter")
    void resolvedAtAfter_ShouldReturnGreaterThanOrEqualTo() {
        LocalDateTime date = LocalDateTime.now();
        Specification<IncidentsEntity> spec = IncidentsSpecification.resolvedAtAfter(date);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(root.get("resolvedAt"), date);
    }

    @Test
    @DisplayName("Should return conjunction when date is null in resolvedAtAfter")
    void resolvedAtAfter_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.resolvedAtAfter(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Should create a lessThanOrEqualTo predicate for resolvedAtBefore")
    void resolvedAtBefore_ShouldReturnLessThanOrEqualTo() {
        LocalDateTime date = LocalDateTime.now();
        Specification<IncidentsEntity> spec = IncidentsSpecification.resolvedAtBefore(date);
        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(root.get("resolvedAt"), date);
    }

    @Test
    @DisplayName("Should return conjunction when date is null in resolvedAtBefore")
    void resolvedAtBefore_ShouldReturnConjunction_WhenNull() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.resolvedAtBefore(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Should create an isNotNull predicate for resolved incidents")
    void isResolved_ShouldReturnIsNotNullPredicate() {
        Specification<IncidentsEntity> spec = IncidentsSpecification.isResolved();
        spec.toPredicate(root, query, cb);

        verify(root).get("resolvedAt");
        verify(cb).isNotNull(path);
    }

    @Test
    @DisplayName("Should combine two specifications using AND")
    void combineTwoSpecs_ShouldCombineWithAnd() {
        Specification<IncidentsEntity> spec1 = IncidentsSpecification.statusEquals(IncidentStatusEnum.OPEN);
        Specification<IncidentsEntity> spec2 = IncidentsSpecification.isNotResolved();

        Specification<IncidentsEntity> combined = IncidentsSpecification.combine(spec1, spec2);
        combined.toPredicate(root, query, cb);

        verify(root).get("status");
        verify(root).get("resolvedAt");
    }

    @Test
    @DisplayName("Should combine three specifications using AND")
    void combineThreeSpecs_ShouldCombineWithAnd() {
        Specification<IncidentsEntity> s1 = IncidentsSpecification.statusEquals(IncidentStatusEnum.OPEN);
        Specification<IncidentsEntity> s2 = IncidentsSpecification.criticalityEquals(CriticalityEnum.HIGH);
        Specification<IncidentsEntity> s3 = IncidentsSpecification.isNotResolved();

        Specification<IncidentsEntity> combined = IncidentsSpecification.combine(s1, s2, s3);
        combined.toPredicate(root, query, cb);

        verify(root).get("status");
        verify(root).get("criticality");
        verify(root).get("resolvedAt");
    }
}