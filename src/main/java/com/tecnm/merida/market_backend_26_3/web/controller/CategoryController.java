package com.tecnm.merida.market_backend_26_3.web.controller;
import com.tecnm.merida.market_backend_26_3.domain.Category;
import com.tecnm.merida.market_backend_26_3.domain.service.CategoryService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
@RestController
@RequestMapping("/categories")
@Tag(name = "Category")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }
    @GetMapping
    public List<Category> getAll() { return service.getAll(); }
    @GetMapping("/{id}")
    public ResponseEntity<Category> get(@PathVariable("id") Integer id) { return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category body) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body)); }
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable("id") Integer id, @RequestBody Category body) { return service.update(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) { return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
