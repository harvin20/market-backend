package com.tecnm.merida.market_backend_26_3.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import com.tecnm.merida.market_backend_26_3.domain.PurchaseRequest.Item;

public record PurchasePatch(
    @Pattern(regexp = ".*\\S.*") String idCliente,
    @Pattern(regexp = "TARJETA|EFECTIVO|T|E", message = "debe ser TARJETA, EFECTIVO, T o E")
    @Schema(allowableValues = {"TARJETA", "EFECTIVO", "T", "E"}) String medioPago,
    String comentario,
    @Size(min = 1) List<@NotNull @Valid Item> productosAdquiridos
) {}
