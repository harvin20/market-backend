package com.tecnm.merida.market_backend_26_3.domain.service;

import com.tecnm.merida.market_backend_26_3.domain.Product;
import com.tecnm.merida.market_backend_26_3.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }
    @org.springframework.transaction.annotation.Transactional
    public Optional<Product> update(int id, Product product) {
        return getProduct(id).map(existing -> { product.setProductId(id); return productRepository.save(product); });
    }
    public List<Product> getAll(){
        return productRepository.getAll();
    }

    public Optional<Product> getProduct(int productId){
        return productRepository.getProduct(productId);
    }

    public Optional<List<Product>>getByCategory(int categoryId){
        return productRepository.getByCategory(categoryId);
    }
    public Product save(Product product){
        product.setProductId(null);
        return productRepository.save(product);
    }
    public boolean delete (int productId){
        //verifica que existe antes de borrar
        if (getProduct(productId).isPresent()) {
            productRepository.delete(productId);
            return true;
        }{
            return false;
        }
    }
}
