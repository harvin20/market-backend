package com.tecnm.merida.market_backend_26_3.persistence;
import com.tecnm.merida.market_backend_26_3.persistence.crud.ProductoCrudRepository;

import com.tecnm.merida.market_backend_26_3.persistence.entity.Producto;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import java.util.List;

public class ProductoRepository {

    private ProductoCrudRepository productoCrudRepository;

   //SELECT * FROM productos
   public List<Producto> getAll(){
       //Vamos a "castear"
       return (List<Producto>) productoCrudRepository.findAll();
   }

}
