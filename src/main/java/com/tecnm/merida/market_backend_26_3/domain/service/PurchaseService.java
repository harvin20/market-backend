package com.tecnm.merida.market_backend_26_3.domain.service;
import com.tecnm.merida.market_backend_26_3.domain.*;
import com.tecnm.merida.market_backend_26_3.persistence.crud.CompraCrudRepository;
import com.tecnm.merida.market_backend_26_3.persistence.entity.*;
import com.tecnm.merida.market_backend_26_3.persistence.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Service
@Transactional(readOnly = true)
public class PurchaseService {
    private final CompraCrudRepository repository;
    private final PurchaseMapper mapper;
    private final PurchaseRequestMapper requestMapper;
    public PurchaseService(CompraCrudRepository repository, PurchaseMapper mapper, PurchaseRequestMapper requestMapper) {
        this.repository = repository; this.mapper = mapper; this.requestMapper = requestMapper;
    }
    public List<Purchase> getAll() { return mapper.toPurchases((List<Compra>) repository.findAll()); }
    public Optional<Purchase> getById(int id) { return repository.findById(id).map(mapper::toPurchase); }
    public List<Purchase> getByClient(String id) { return mapper.toPurchases(repository.findByIdCliente(id).orElseGet(List::of)); }
    private Compra map(PurchaseRequest request) {
        Set<Integer> ids = new HashSet<>();
        for (var item : request.productosAdquiridos()) {
            if (!ids.add(item.idProducto())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No repita productos en el detalle");
        }
        Compra compra = requestMapper.toCompra(request);
        compra.getProductos().forEach(item -> item.setCompra(compra));
        return compra;
    }
    @Transactional
    public Purchase save(PurchaseRequest request) {
        return mapper.toPurchase(repository.save(map(request)));
    }
    @Transactional
    public Optional<Purchase> update(int id, PurchaseRequest request) {
        return repository.findById(id).map(existing -> {
            Compra incoming = map(request);
            existing.setIdCliente(incoming.getIdCliente());
            existing.setMedioPago(incoming.getMedioPago());
            existing.setComentario(incoming.getComentario());
            // Reconcile by composite key to retain existing rows and remove omitted details.
            existing.getProductos().removeIf(old -> incoming.getProductos().stream().noneMatch(item -> item.getId().getIdProducto().equals(old.getId().getIdProducto())));
            for (CompraProducto item : incoming.getProductos()) {
                var old = existing.getProductos().stream().filter(p -> p.getId().getIdProducto().equals(item.getId().getIdProducto())).findFirst();
                if (old.isPresent()) {
                    old.get().setCantidad(item.getCantidad()); old.get().setTotal(item.getTotal());
                } else {
                    item.setCompra(existing); existing.getProductos().add(item);
                }
            }
            return mapper.toPurchase(repository.save(existing));
        });
    }
    @Transactional
    public boolean delete(int id) { if (!repository.existsById(id)) return false; repository.deleteById(id); return true; }
}
