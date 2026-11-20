package com.tecnm.merida.market_backend_26_3.persistence.mapper;
import com.tecnm.merida.market_backend_26_3.domain.PurchaseRequest;
import com.tecnm.merida.market_backend_26_3.persistence.entity.*;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface PurchaseRequestMapper {
    @Mapping(target = "idCompra", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "fecha", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "estado", constant = "P")
    @Mapping(target = "productos", source = "productosAdquiridos")
    @Mapping(target = "medioPago", source = "medioPago", qualifiedByName = "paymentCode")
    Compra toCompra(PurchaseRequest request);
    @Named("paymentCode")
    default String paymentCode(String value) {
        if (value != null) {
            switch (value) {
                case "TARJETA", "T": return "T";
                case "EFECTIVO", "E": return "E";
            }
        }
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.BAD_REQUEST,
            "medioPago debe ser TARJETA, EFECTIVO, T o E");
    }
    @Mapping(target = "id.idProducto", source = "idProducto")
    @Mapping(target = "id.idCompra", ignore = true)
    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "estado", constant = "true")
    CompraProducto toItem(PurchaseRequest.Item item);
}
