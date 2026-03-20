package com.rodrigues.heric.incidentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateUsersRequest;
import com.rodrigues.heric.incidentmanager.dto.UsersDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsersMapper {

    UsersDTO toDTO(UsersEntity user);

    @Mapping(target = "id", ignore = true)
    UsersEntity toEntity(CreateUsersRequest user);

}
