package com.tecnm.merida.market_backend_26_3.domain.service;
import com.tecnm.merida.market_backend_26_3.domain.Client;
import com.tecnm.merida.market_backend_26_3.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend_26_3.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
@Transactional(readOnly = true)
public class ClientService {
    private final ClienteCrudRepository repository;
    private final ClientMapper mapper;
    public ClientService(ClienteCrudRepository repository, ClientMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<Client> getAll() { return repository.findAll().stream().map(mapper::toClient).toList(); }
    public Optional<Client> getById(String id) { return repository.findById(id).map(mapper::toClient); }
    @Transactional
    public Client create(Client body) { if (body.getId() == null || body.getId().isBlank()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "El ID del cliente es obligatorio");
        if (repository.existsById(body.getId())) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El cliente ya existe"); return mapper.toClient(repository.save(mapper.toCliente(body))); }
    @Transactional
    public Optional<Client> update(String id, Client body) {
        return repository.findById(id).map(old -> { body.setId(id); return mapper.toClient(repository.save(mapper.toCliente(body))); });
    }
    @Transactional
    public Optional<Client> patch(String id, com.tecnm.merida.market_backend_26_3.domain.ClientPatch body) {
        return getById(id).map(existing -> {
            if (body.apellidos() != null) existing.setApellidos(body.apellidos());
            if (body.celular() != null) existing.setCelular(body.celular());
            if (body.dirreccion() != null) existing.setDirreccion(body.dirreccion());
            if (body.correoElectronico() != null) existing.setCorreoElectronico(body.correoElectronico());
            return mapper.toClient(repository.save(mapper.toCliente(existing)));
        });
    }
    @Transactional
    public boolean delete(String id) { if (!repository.existsById(id)) return false; repository.deleteById(id); return true; }
}
