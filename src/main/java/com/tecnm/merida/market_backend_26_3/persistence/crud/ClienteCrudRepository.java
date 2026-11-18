package com.tecnm.merida.market_backend_26_3.persistence.crud;
import com.tecnm.merida.market_backend_26_3.persistence.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteCrudRepository extends JpaRepository<Cliente, String> {}
