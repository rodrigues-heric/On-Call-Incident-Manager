package com.rodrigues.heric.incidentmanager.mapper;

import org.mapstruct.Mapper;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.UsersDTO;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    UsersDTO toDTO(UsersEntity user);

    UsersEntity toEntity(UsersDTO user);

}
