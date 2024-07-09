package com.supplyhouse.account_management.mapper;

import com.supplyhouse.account_management.dto.RequestAccountDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface UserAccountMapper {

    ResponseAccountDto toResponseAccountDto(UserAccount account);

    List<ResponseAccountDto> toResponseAccountDtoList(List<UserAccount> accounts);

    @Mapping(target = "password", ignore = true)
    UserAccount toUserAccount(RequestAccountDto requestAccountDto);


}
