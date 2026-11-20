package com.tecnm.merida.market_backend_26_3.web.controller;
import com.tecnm.merida.market_backend_26_3.domain.*;
import com.tecnm.merida.market_backend_26_3.domain.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchase", description = "Compras y detalles guardados en una transaccion")
public class PurchaseController {
    private final PurchaseService service;
    public PurchaseController(PurchaseService service) { this.service = service; }
    @GetMapping
    public List<Purchase> getAll() { return service.getAll(); }
    @GetMapping("/{id}")
    public ResponseEntity<Purchase> get(@PathVariable("id") int id) { return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @GetMapping("/client/{id}")
    public List<Purchase> getByClient(@PathVariable("id") String id) { return service.getByClient(id); }
    @PostMapping
    public ResponseEntity<Purchase> create(@Valid @RequestBody PurchaseRequest body) { return ResponseEntity.status(HttpStatus.CREATED).body(service.save(body)); }
    @PatchMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar parcialmente", description = "Los campos omitidos o null conservan su valor. En compras, productosAdquiridos reemplaza la lista completa si se envia.")
    public ResponseEntity<Purchase> patch(@PathVariable("id") int id,
            @jakarta.validation.Valid @RequestBody com.tecnm.merida.market_backend_26_3.domain.PurchasePatch body) {
        return service.patch(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Purchase> update(@PathVariable("id") int id, @Valid @RequestBody PurchaseRequest body) { return service.update(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) { return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
