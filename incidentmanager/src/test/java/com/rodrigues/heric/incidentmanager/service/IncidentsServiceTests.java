package com.rodrigues.heric.incidentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.domain.state.IncidentStateMachine;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.exception.InvalidStateTransitionException;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.IncidentsMapper;
import com.rodrigues.heric.incidentmanager.repository.IncidentEventsRepository;
import com.rodrigues.heric.incidentmanager.repository.IncidentsRepository;
import com.rodrigues.heric.incidentmanager.repository.ServicesRepository;
import com.rodrigues.heric.incidentmanager.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class IncidentsServiceTests {

	@Mock
	IncidentsRepository incidentsRepository;
	@Mock
	IncidentEventsRepository incidentEventsRepository;
	@Mock
	ServicesRepository servicesRepository;
	@Mock
	UsersRepository usersRepository;
	@Mock
	IncidentsMapper incidentsMapper;
	@Mock
	IncidentStateMachine stateMachine;

	@InjectMocks
	IncidentsService incidentsService;

	// ========== CREATE INCIDENT ==========
	@Test
	@DisplayName("Should create incident successfully")
	public void shouldCreateIncidentSuccessfully() {
		UUID incidentId = UUID.randomUUID();
		UUID serviceId = UUID.randomUUID();
		String title = "Incident title";
		String description = "Incident description";
		CriticalityEnum criticality = CriticalityEnum.CRITICAL;

		ServicesEntity service = ServicesEntity.builder()
				.id(serviceId)
				.name("Service Test")
				.build();

		CreateIncidentsRequest request = new CreateIncidentsRequest(
				title,
				description,
				criticality,
				serviceId);

		when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(service));

		IncidentsEntity incidentsEntity = IncidentsEntity.builder()
				.title(title)
				.description(description)
				.criticality(criticality)
				.service(service)
				.status(IncidentStatusEnum.OPEN)
				.build();

		IncidentsEntity savedIncident = IncidentsEntity.builder()
				.id(incidentId)
				.title(title)
				.description(description)
				.criticality(criticality)
				.service(service)
				.status(IncidentStatusEnum.OPEN)
				.build();

		IncidentsDTO expectedDTO = new IncidentsDTO(
				incidentId,
				title,
				description,
				IncidentStatusEnum.OPEN,
				criticality,
				serviceId,
				null,
				null);

		when(incidentsMapper.toEntity(request)).thenReturn(incidentsEntity);
		when(incidentsRepository.save(incidentsEntity)).thenReturn(savedIncident);
		when(incidentsMapper.toDTO(savedIncident)).thenReturn(expectedDTO);

		IncidentsDTO result = incidentsService.createIncident(request);

		assertNotNull(result);
		assertEquals(expectedDTO.id(), result.id());
		assertEquals(expectedDTO.title(), result.title());
		assertEquals(expectedDTO.description(), result.description());
		assertEquals(expectedDTO.status(), result.status());
		assertEquals(expectedDTO.criticality(), result.criticality());
		assertEquals(expectedDTO.serviceId(), result.serviceId());
		assertEquals(expectedDTO.assigneeId(), result.assigneeId());
		assertEquals(expectedDTO.resolvedAt(), result.resolvedAt());

		verify(servicesRepository, times(1)).findById(serviceId);
		verify(incidentsMapper, times(1)).toEntity(request);
		verify(incidentsRepository, times(1)).save(incidentsEntity);
		verify(incidentsMapper, times(1)).toDTO(savedIncident);
	}

	@Test
	@DisplayName("Should throw exception when service not found")
	public void shouldThrowExceptionWhenServiceNotFound() {
		UUID serviceId = UUID.randomUUID();

		CreateIncidentsRequest request = new CreateIncidentsRequest(
				"Title",
				"Description",
				CriticalityEnum.CRITICAL, serviceId);

		when(this.servicesRepository.findById(serviceId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> this.incidentsService.createIncident(request));

		verify(this.servicesRepository, times(1)).findById(serviceId);
		verify(this.incidentsRepository, never()).save(any());
	}

	// ========== GET INCIDENT ==========
	@Test
	@DisplayName("Should fetch incident by id successfully")
	public void shouldFetchIncidentByIdSuccessfully() {
		UUID id = UUID.randomUUID();
		String title = "Title";
		String description = "Description";
		CriticalityEnum criticality = CriticalityEnum.LOW;

		UUID serviceId = UUID.randomUUID();
		ServicesEntity service = ServicesEntity.builder()
				.id(serviceId)
				.name("Service Test")
				.build();

		IncidentsEntity incidentsEntity = IncidentsEntity.builder()
				.id(id)
				.title(title)
				.description(description)
				.criticality(criticality)
				.service(service)
				.status(IncidentStatusEnum.OPEN)
				.build();
		IncidentsDTO expectedDTO = new IncidentsDTO(
				id,
				title,
				description,
				IncidentStatusEnum.OPEN,
				criticality,
				serviceId,
				null,
				null);

		when(this.incidentsRepository.findById(id)).thenReturn(Optional.of(incidentsEntity));
		when(this.incidentsMapper.toDTO(incidentsEntity)).thenReturn(expectedDTO);

		IncidentsDTO result = this.incidentsService.getIncidentById(id);

		assertNotNull(result);
		assertEquals(expectedDTO.id(), result.id());
		assertEquals(expectedDTO.title(), result.title());
		assertEquals(expectedDTO.description(), result.description());
		assertEquals(expectedDTO.status(), result.status());
		assertEquals(expectedDTO.criticality(), result.criticality());
		assertEquals(expectedDTO.serviceId(), result.serviceId());
		assertEquals(expectedDTO.assigneeId(), result.assigneeId());
		assertEquals(expectedDTO.resolvedAt(), result.resolvedAt());
	}

	@Test
	@DisplayName("Should throw exception when no incident found")
	public void shouldThrowExceptionWhenNoIncidentFound() {
		UUID id = UUID.randomUUID();

		when(this.incidentsRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> this.incidentsService.getIncidentById(id));

		verify(this.incidentsMapper, never()).toDTO(any());
	}

	// ========== SEARCH INCIDENT ==========
	@Test
	@DisplayName("Should find all incidents with filters successfully")
	public void shouldFindAllIncidentsWithFiltersSuccessfully() {
		IncidentStatusEnum status = IncidentStatusEnum.OPEN;
		CriticalityEnum criticality = CriticalityEnum.HIGH;
		UUID serviceId = UUID.randomUUID();
		UUID assigneeId = UUID.randomUUID();
		String title = "Database";

		IncidentsEntity entity = IncidentsEntity.builder()
				.id(UUID.randomUUID())
				.title("Database Error")
				.status(status)
				.criticality(criticality)
				.build();

		IncidentsDTO dto = new IncidentsDTO(
				entity.getId(),
				entity.getTitle(),
				"Description",
				status,
				criticality,
				serviceId,
				assigneeId,
				null);

		when(this.incidentsRepository.findAll(ArgumentMatchers.<Specification<IncidentsEntity>>any()))
				.thenReturn(List.of(entity));
		when(this.incidentsMapper.toDTO(entity)).thenReturn(dto);

		List<IncidentsDTO> result = this.incidentsService.findAllWithFilters(
				status, serviceId, criticality, assigneeId, title);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(dto.id(), result.get(0).id());
		assertEquals(dto.title(), result.get(0).title());

		verify(this.incidentsRepository, times(1))
				.findAll(ArgumentMatchers.<Specification<IncidentsEntity>>any());
		verify(this.incidentsMapper, times(1)).toDTO(entity);
	}

	@Test
	@DisplayName("Should return empty list when no incidents match filters")
	public void shouldReturnEmptyListWhenNoIncidentsMatchFilters() {
		when(this.incidentsRepository.findAll(ArgumentMatchers.<Specification<IncidentsEntity>>any()))
				.thenReturn(java.util.Collections.emptyList());

		List<IncidentsDTO> result = this.incidentsService.findAllWithFilters(
				null, null, null, null, null);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(this.incidentsRepository, times(1))
				.findAll(ArgumentMatchers.<Specification<IncidentsEntity>>any());
		verify(this.incidentsMapper, never()).toDTO(any());
	}

	// ========== UPDATE INCIDENT ==========
	@Test
	@DisplayName("Should return IncidentsEntity when id exists")
	void getIncidentsEntity_WhenIdExists_ReturnsEntity() {
		UUID incidentId = UUID.randomUUID();
		IncidentsEntity expectedEntity = new IncidentsEntity();
		expectedEntity.setId(incidentId);

		when(incidentsRepository.findById(incidentId)).thenReturn(Optional.of(expectedEntity));

		IncidentsEntity result = ReflectionTestUtils.invokeMethod(incidentsService, "getIncidentsEntity",
				incidentId);

		assertNotNull(result);
		assertEquals(incidentId, result.getId());
		verify(incidentsRepository, times(1)).findById(incidentId);
	}

	@Test
	@DisplayName("Should throw ResourceNotFoundException when id does not exist")
	void getIncidentsEntity_WhenIdDoesNotExist_ThrowsException() {
		UUID incidentId = UUID.randomUUID();
		String expectedMessage = "Incident with id " + incidentId + " not found";

		when(incidentsRepository.findById(incidentId)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			ReflectionTestUtils.invokeMethod(incidentsService, "getIncidentsEntity", incidentId);
		});

		assertEquals(expectedMessage, exception.getMessage());
		verify(incidentsRepository, times(1)).findById(incidentId);
	}

	@Test
	@DisplayName("Should set resolvedAt when status is RESOLVED")
	void setResolvedAt_WhenStatusIsResolved_SetsDate() {
		IncidentsEntity entity = new IncidentsEntity();
		entity.setResolvedAt(null);
		IncidentStatusEnum status = IncidentStatusEnum.RESOLVED;

		ReflectionTestUtils.invokeMethod(incidentsService, "setResolvedAt", entity, status);

		assertNotNull(entity.getResolvedAt());
		assertTrue(entity.getResolvedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
	}

	@Test
	@DisplayName("Should not set resolvedAt when status is not RESOLVED")
	void setResolvedAt_WhenStatusIsNotResolved_DoesNothing() {
		IncidentsEntity entity = new IncidentsEntity();
		entity.setResolvedAt(null);
		IncidentStatusEnum status = IncidentStatusEnum.ACKNOWLEDGED;

		ReflectionTestUtils.invokeMethod(incidentsService, "setResolvedAt", entity, status);

		assertNull(entity.getResolvedAt(), "ResolvedAt should remain null for non-resolved status");
	}

	@Test
	@DisplayName("Should successfully update incident status and return DTO")
	void updateIncidentStatus_Success() {
		UUID id = UUID.randomUUID();
		UUID serviceId = UUID.randomUUID();
		IncidentStatusEnum newStatus = IncidentStatusEnum.ACKNOWLEDGED;

		IncidentsEntity existingEntity = new IncidentsEntity();
		existingEntity.setId(id);
		existingEntity.setStatus(IncidentStatusEnum.OPEN);

		IncidentsDTO expectedDto = new IncidentsDTO(
				id,
				"Database Connection Failure",
				"High latency in production",
				newStatus,
				CriticalityEnum.HIGH,
				serviceId,
				null,
				null);

		when(incidentsRepository.findById(id)).thenReturn(Optional.of(existingEntity));
		when(incidentsRepository.save(any(IncidentsEntity.class))).thenReturn(existingEntity);
		when(incidentsMapper.toDTO(any(IncidentsEntity.class))).thenReturn(expectedDto);

		IncidentsDTO result = incidentsService.updateIncidentStatus(id, newStatus);

		assertNotNull(result);
		assertEquals(newStatus, result.status());
		assertEquals(id, result.id());

		verify(stateMachine).validateTransition(IncidentStatusEnum.OPEN, newStatus);
		verify(incidentsRepository).save(existingEntity);
		verify(incidentsMapper).toDTO(existingEntity);
	}

	@Test
	@DisplayName("Should throw exception and not save when FSM validation fails")
	void updateIncidentStatus_InvalidTransition_ThrowsException() {
		UUID id = UUID.randomUUID();
		IncidentStatusEnum currentStatus = IncidentStatusEnum.OPEN;
		IncidentStatusEnum invalidNextStatus = IncidentStatusEnum.RESOLVED;

		IncidentsEntity entity = new IncidentsEntity();
		entity.setStatus(currentStatus);

		when(incidentsRepository.findById(id)).thenReturn(Optional.of(entity));

		doThrow(new InvalidStateTransitionException("Invalid transition"))
				.when(stateMachine).validateTransition(currentStatus, invalidNextStatus);

		assertThrows(InvalidStateTransitionException.class, () -> {
			incidentsService.updateIncidentStatus(id, invalidNextStatus);
		});

		verify(incidentsRepository, never()).save(any());
		verifyNoInteractions(incidentsMapper);
	}

}
