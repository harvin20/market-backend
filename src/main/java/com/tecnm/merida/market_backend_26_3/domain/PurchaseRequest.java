package com.tecnm.merida.market_backend_26_3.domain;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
public record PurchaseRequest(
    @NotBlank @Schema(example = "CLI-001") String idCliente,
    @NotBlank @Schema(example = "TARJETA") String medioPago,
    @Schema(example = "Entregar por la tarde") String comentario,
    @NotEmpty List<@NotNull @Valid Item> productosAdquiridos
) {
    public record Item(@NotNull @Positive Integer idProducto,
                       @NotNull @Positive Integer cantidad,
                       @NotNull @PositiveOrZero Double total) {}
}
