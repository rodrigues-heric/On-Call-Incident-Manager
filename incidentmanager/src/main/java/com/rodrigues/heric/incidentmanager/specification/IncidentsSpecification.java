package com.rodrigues.heric.incidentmanager.specification;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;

public class IncidentsSpecification {

    private IncidentsSpecification() {
    }

    public static Specification<IncidentsEntity> statusEquals(IncidentStatusEnum status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<IncidentsEntity> serviceIdEquals(UUID serviceId) {
        return (root, query, criteriaBuilder) -> {
            if (serviceId == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("service").get("id"), serviceId);
        };
    }

    public static Specification<IncidentsEntity> criticalityEquals(CriticalityEnum criticality) {
        return (root, query, criteriaBuilder) -> {
            if (criticality == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("criticality"), criticality);
        };
    }

    public static Specification<IncidentsEntity> assigneeIdEquals(UUID assigneeId) {
        return (root, query, criteriaBuilder) -> {
            if (assigneeId == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    public static Specification<IncidentsEntity> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank())
                return criteriaBuilder.conjunction();
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<IncidentsEntity> createdAtAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<IncidentsEntity> createdAtBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<IncidentsEntity> resolvedAtAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("resolvedAt"), date);
        };
    }

    public static Specification<IncidentsEntity> resolvedAtBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.lessThanOrEqualTo(root.get("resolvedAt"), date);
        };
    }

    public static Specification<IncidentsEntity> isNotResolved() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("resolvedAt"));
    }

    public static Specification<IncidentsEntity> isResolved() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("resolvedAt"));
    }

    public static Specification<IncidentsEntity> withFilters(
            IncidentStatusEnum status,
            UUID serviceId,
            CriticalityEnum criticality,
            UUID assigneeId,
            String title) {

        return Specification.where(statusEquals(status))
                .and(serviceIdEquals(serviceId))
                .and(criticalityEquals(criticality))
                .and(assigneeIdEquals(assigneeId))
                .and(titleContains(title));
    }

    public static Specification<IncidentsEntity> combine(
            Specification<IncidentsEntity> spec1,
            Specification<IncidentsEntity> spec2) {

        return Specification.where(spec1).and(spec2);
    }

    public static Specification<IncidentsEntity> combine(
            Specification<IncidentsEntity> spec1,
            Specification<IncidentsEntity> spec2,
            Specification<IncidentsEntity> spec3) {

        return Specification.where(spec1).and(spec2).and(spec3);
    }

}
