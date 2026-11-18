package com.tecnm.merida.market_backend_26_3.persistence.mapper;
import com.tecnm.merida.market_backend_26_3.domain.Client;
import com.tecnm.merida.market_backend_26_3.persistence.entity.Cliente;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toClient(Cliente entity);
    @Mapping(target = "compras", ignore = true)
    Cliente toCliente(Client client);
}
