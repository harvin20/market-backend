package com.tecnm.merida.market_backend_26_3.persistence;
import com.tecnm.merida.market_backend_26_3.domain.Product;
import com.tecnm.merida.market_backend_26_3.domain.repository.ProductRepository;
import com.tecnm.merida.market_backend_26_3.persistence.crud.ProductoCrudRepository;
import com.tecnm.merida.market_backend_26_3.persistence.mapper.ProductMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class ProductoRepository implements ProductRepository {
    private final ProductoCrudRepository repository;
    private final ProductMapper mapper;
    public ProductoRepository(ProductoCrudRepository repository, ProductMapper mapper) {
        this.repository = repository; this.mapper = mapper;
    }
    public List<Product> getAll() { return mapper.toProducts((List<com.tecnm.merida.market_backend_26_3.persistence.entity.Producto>) repository.findAll()); }
    public Optional<Product> getProduct(int id) { return repository.findById(id).map(mapper::toProduct); }
    public Optional<List<Product>> getByCategory(int id) { return Optional.of(mapper.toProducts(repository.findByIdCategoriaOrderByNombreAsc(id))); }
    public Optional<List<Product>> getScarceProducts(int quantity) { return repository.findByCantidadStockLessThanAndEstado(quantity, true).map(mapper::toProducts); }
    public Product save(Product product) {
        var entity = mapper.toProducto(product);
        if (product.getProductId() != null) repository.findById(product.getProductId()).ifPresent(old -> entity.setCodigoBarras(old.getCodigoBarras()));
        return mapper.toProduct(repository.save(entity));
    }
    public void delete(int id) { repository.deleteById(id); }
}
