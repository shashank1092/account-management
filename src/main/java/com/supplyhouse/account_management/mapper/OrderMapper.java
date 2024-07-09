package com.supplyhouse.account_management.mapper;

import com.supplyhouse.account_management.dto.RequestOrderDto;
import com.supplyhouse.account_management.dto.ResponseOrderDto;
import com.supplyhouse.account_management.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface OrderMapper {
    @Mapping(source = "account.accountId", target = "accountId")
    ResponseOrderDto toResponseOrderDto(Order order);

    List<ResponseOrderDto> toResponseOrderDtoList(List<Order> order);

    @Mapping(target = "account.accountId", source = "accountId")
    Order toOrder(RequestOrderDto requestOrderDto);

}
