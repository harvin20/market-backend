package com.tecnm.merida.market_backend_26_3.web.controller;
import com.tecnm.merida.market_backend_26_3.domain.Client;
import com.tecnm.merida.market_backend_26_3.domain.service.ClientService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
@RestController
@RequestMapping("/clients")
@Tag(name = "Client")
public class ClientController {
    private final ClientService service;
    public ClientController(ClientService service) { this.service = service; }
    @GetMapping
    public List<Client> getAll() { return service.getAll(); }
    @GetMapping("/{id}")
    public ResponseEntity<Client> get(@PathVariable("id") String id) { return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client body) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body)); }
    @PatchMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar parcialmente", description = "Los campos omitidos o null conservan su valor. En compras, productosAdquiridos reemplaza la lista completa si se envia.")
    public ResponseEntity<Client> patch(@PathVariable("id") String id,
            @jakarta.validation.Valid @RequestBody com.tecnm.merida.market_backend_26_3.domain.ClientPatch body) {
        return service.patch(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable("id") String id, @RequestBody Client body) { return service.update(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) { return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
