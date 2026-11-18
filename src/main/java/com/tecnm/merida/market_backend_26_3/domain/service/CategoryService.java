package com.tecnm.merida.market_backend_26_3.domain.service;
import com.tecnm.merida.market_backend_26_3.domain.Category;
import com.tecnm.merida.market_backend_26_3.persistence.crud.CategoriaCrudRepository;
import com.tecnm.merida.market_backend_26_3.persistence.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoriaCrudRepository repository;
    private final CategoryMapper mapper;
    public CategoryService(CategoriaCrudRepository repository, CategoryMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<Category> getAll() { return repository.findAll().stream().map(mapper::toCategory).toList(); }
    public Optional<Category> getById(Integer id) { return repository.findById(id).map(mapper::toCategory); }
    @Transactional
    public Category create(Category body) { body.setCategoryId(null); return mapper.toCategory(repository.save(mapper.toCategoria(body))); }
    @Transactional
    public Optional<Category> update(Integer id, Category body) {
        return repository.findById(id).map(old -> { body.setCategoryId(id); return mapper.toCategory(repository.save(mapper.toCategoria(body))); });
    }
    @Transactional
    public boolean delete(Integer id) { if (!repository.existsById(id)) return false; repository.deleteById(id); return true; }
}
