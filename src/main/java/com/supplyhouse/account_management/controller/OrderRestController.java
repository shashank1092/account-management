package com.supplyhouse.account_management.controller;

import com.supplyhouse.account_management.dto.RequestOrderDto;
import com.supplyhouse.account_management.dto.ResponseOrderDto;
import com.supplyhouse.account_management.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping(("order"))
@Tag(name = "Order", description = "Endpoints related to order")
public class OrderRestController {

    OrderService orderService;

    @Autowired
    public OrderRestController(OrderService orderService){
        this.orderService = orderService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get order details by id", description = "Get order details for the given order id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order fetched successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseOrderDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content)
    })
    public ResponseEntity<ResponseOrderDto> getOrderById(@PathVariable @NotNull Long id) {
        ResponseOrderDto ordersForIndividualAccount = orderService.getOrderById(id);
        return new ResponseEntity<>(ordersForIndividualAccount, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Place new order", description = "Creates a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseOrderDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content)
    })
    public ResponseEntity<ResponseOrderDto> placeNewOrder(@RequestBody @Validated RequestOrderDto requestOrderDto) {
        ResponseOrderDto responseDto = orderService.createOrder(requestOrderDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all orders linked to account ", description = "This api will fetch all individual orders with sub account orders if present")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders fetched successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseOrderDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content)
    })
    public ResponseEntity<List<ResponseOrderDto>> getAllOrders() throws AccountNotFoundException {
        List<ResponseOrderDto> ordersForIndividualAccount = orderService.getAllOrdersForLoggedInUser();
        return new ResponseEntity<>(ordersForIndividualAccount, HttpStatus.OK);
    }
}
