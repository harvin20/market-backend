package com.tecnm.merida.market_backend_26_3;

import com.tecnm.merida.market_backend_26_3.domain.*;
import com.tecnm.merida.market_backend_26_3.domain.service.*;
import com.tecnm.merida.market_backend_26_3.web.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class MarketBackend263ApplicationTests {
    @Autowired CategoryService categories;
    @Autowired ClientService clients;
    @Autowired ProductService products;
    @Autowired PurchaseService purchases;
    @Autowired JdbcTemplate jdbc;
    @Autowired CategoryController categoryController;
    @Autowired ClientController clientController;
    @Autowired ProductController productController;
    @Autowired PurchaseController purchaseController;
    MockMvc mvc;
    int categoryId, firstId, secondId;

    @BeforeEach
    void setup() {
        jdbc.update("delete from compras_productos");
        jdbc.update("delete from compras");
        jdbc.update("delete from productos");
        jdbc.update("delete from categorias");
        jdbc.update("delete from clientes");
        mvc = MockMvcBuilders.standaloneSetup(categoryController, clientController, productController, purchaseController)
            .setControllerAdvice(new ApiExceptionHandler()).build();
        Category category = new Category(); category.setCategory("Alimentos"); category.setActive(true);
        categoryId = categories.create(category).getCategoryId();
        Client client = new Client(); client.setId("CLI-001"); client.setApellidos("Perez"); clients.create(client);
        firstId = product("Arroz"); secondId = product("Leche");
    }
    int product(String name) {
        Product p = new Product(); p.setName(name); p.setCategoryId(categoryId); p.setPrice(25); p.setStock(100); p.setActive(true);
        return products.save(p).getProductId();
    }
    PurchaseRequest request(int second) {
        return new PurchaseRequest("CLI-001", "TARJETA", "Entrega", List.of(
            new PurchaseRequest.Item(firstId, 2, 50.0), new PurchaseRequest.Item(second, 1, 25.0)));
    }
    String json(int second) {
        return """
          {"idCliente":"CLI-001","medioPago":"TARJETA","comentario":"Entrega",
           "productosAdquiridos":[{"idProducto":%d,"cantidad":2,"total":50.0},{"idProducto":%d,"cantidad":1,"total":25.0}]}
          """.formatted(firstId, second);
    }
    @Test
    void purchaseCascadeUpdateAndDelete() throws Exception {
        mvc.perform(post("/purchases").contentType(MediaType.APPLICATION_JSON).content(json(secondId)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.items.length()").value(2));
        int id = purchases.getAll().getFirst().getPurchaseId();
        assertEquals(2, jdbc.queryForObject("select count(*) from compras_productos where id_compra=?", Integer.class, id));
        mvc.perform(get("/purchases")).andExpect(status().isOk());
        mvc.perform(get("/purchases/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/purchases/client/CLI-001")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        String replacement = """
            {"idCliente":"CLI-001","medioPago":"EFECTIVO","productosAdquiridos":[{"idProducto":%d,"cantidad":3,"total":75}]}
            """.formatted(firstId);
        mvc.perform(put("/purchases/{id}", id).contentType(MediaType.APPLICATION_JSON).content(replacement))
            .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1));
        assertEquals(1, jdbc.queryForObject("select count(*) from compras_productos", Integer.class));
        assertEquals(3, purchases.getById(id).orElseThrow().getItems().getFirst().getQuantity());
        mvc.perform(put("/purchases/{id}", id).contentType(MediaType.APPLICATION_JSON).content(json(secondId))).andExpect(status().isOk());
        assertEquals(2, jdbc.queryForObject("select count(*) from compras_productos", Integer.class));
        mvc.perform(delete("/purchases/{id}", id)).andExpect(status().isNoContent());
        assertEquals(0, jdbc.queryForObject("select count(*) from compras_productos", Integer.class));
        mvc.perform(get("/purchases/{id}", id)).andExpect(status().isNotFound());
    }
    @Test
    void failedSecondDetailRollsBackHeaderAndFirstDetail() throws Exception {
        mvc.perform(post("/purchases").contentType(MediaType.APPLICATION_JSON).content(json(999999)))
            .andExpect(status().isConflict());
        assertEquals(0, jdbc.queryForObject("select count(*) from compras", Integer.class));
        assertEquals(0, jdbc.queryForObject("select count(*) from compras_productos", Integer.class));
    }
    @Test
    void failedUpdatePreservesOriginalPurchase() throws Exception {
        int id = purchases.save(request(secondId)).getPurchaseId();
        mvc.perform(put("/purchases/{id}", id).contentType(MediaType.APPLICATION_JSON).content(json(999999)))
            .andExpect(status().isConflict());
        assertEquals(2, purchases.getById(id).orElseThrow().getItems().size());
        assertTrue(purchases.getById(id).orElseThrow().getItems().stream().anyMatch(item -> item.getProductId() == secondId));
    }
    @Test
    void invalidPurchaseAndMissingIds() throws Exception {
        for (String body : List.of(json(firstId), "{\"idCliente\":\"CLI-001\",\"medioPago\":\"TARJETA\",\"productosAdquiridos\":[]}", json(secondId).replace("\"cantidad\":2", "\"cantidad\":0"))) {
            mvc.perform(post("/purchases").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        }
        for (String path : List.of("/products/999999", "/categories/999999", "/clients/missing", "/purchases/999999")) {
            mvc.perform(get(path)).andExpect(status().isNotFound());
            mvc.perform(delete(path)).andExpect(status().isNotFound());
        }
    }
    @Test
    void catalogCrudAndFilters() throws Exception {
        String cat = "{\"category\":\"Bebidas\",\"active\":true}";
        mvc.perform(post("/categories").contentType(MediaType.APPLICATION_JSON).content(cat)).andExpect(status().isCreated());
        mvc.perform(get("/categories")).andExpect(status().isOk());
        mvc.perform(get("/categories/{id}", categoryId)).andExpect(status().isOk());
        mvc.perform(put("/categories/{id}", categoryId).contentType(MediaType.APPLICATION_JSON).content(cat)).andExpect(status().isOk()).andExpect(jsonPath("$.category").value("Bebidas"));
        String client = "{\"id\":\"CLI-002\",\"apellidos\":\"Lopez\"}";
        mvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content(client)).andExpect(status().isCreated());
        mvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content(client)).andExpect(status().isConflict());
        mvc.perform(get("/clients")).andExpect(status().isOk());
        mvc.perform(get("/clients/CLI-002")).andExpect(status().isOk());
        mvc.perform(put("/clients/CLI-002").contentType(MediaType.APPLICATION_JSON).content(client.replace("Lopez", "Gomez"))).andExpect(status().isOk());
        assertEquals("Gomez", clients.getById("CLI-002").orElseThrow().getApellidos());
        String prod = "{\"name\":\"Cafe\",\"categoryId\":%d,\"price\":30,\"stock\":5,\"active\":true}".formatted(categoryId);
        mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(prod)).andExpect(status().isCreated());
        mvc.perform(get("/products")).andExpect(status().isOk());
        mvc.perform(get("/products/{id}", firstId)).andExpect(status().isOk());
        mvc.perform(get("/products/category/{id}", categoryId)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mvc.perform(put("/products/{id}", firstId).contentType(MediaType.APPLICATION_JSON).content(prod)).andExpect(status().isOk());
        assertEquals("Cafe", products.getProduct(firstId).orElseThrow().getName());
        mvc.perform(put("/products/999999").contentType(MediaType.APPLICATION_JSON).content(prod)).andExpect(status().isNotFound());
        mvc.perform(put("/categories/999999").contentType(MediaType.APPLICATION_JSON).content(cat)).andExpect(status().isNotFound());
        mvc.perform(put("/clients/missing").contentType(MediaType.APPLICATION_JSON).content(client)).andExpect(status().isNotFound());
        mvc.perform(put("/purchases/999999").contentType(MediaType.APPLICATION_JSON).content(json(secondId))).andExpect(status().isNotFound());
        for (Product p : products.getAll()) mvc.perform(delete("/products/{id}", p.getProductId())).andExpect(status().isOk());
        mvc.perform(delete("/categories/{id}", categoryId)).andExpect(status().isNoContent());
        mvc.perform(delete("/clients/CLI-002")).andExpect(status().isNoContent());
    }
}
