package com.sia.salesapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sia.salesapp.web.dto.ProductCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllProducts_ShouldReturnListOfProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetProductById_ShouldReturnProduct_WhenExists() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateProduct_ShouldReturnCreatedProduct() throws Exception {
        long timestamp = System.currentTimeMillis();

        ProductCreateRequest request = new ProductCreateRequest(
                "TEST-SKU-" + timestamp,         // sku
                "Test Product " + timestamp,     // name
                "Description for REST testing",  // description
                1L,                              // brandId (MUTAT AICI)
                2L,                              // categoryId (MUTAT AICI)
                new BigDecimal("999.99"),        // price
                "RON",                           // currency
                new BigDecimal("19.00")          // vatRate
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST-SKU-" + timestamp))
                .andExpect(jsonPath("$.name").value("Test Product " + timestamp));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateProductWithInventory_ShouldReturnProductAndInventory() throws Exception {
        long timestamp = System.currentTimeMillis();

        ProductCreateRequest request = new ProductCreateRequest(
                "WORKFLOW-SKU-" + timestamp,
                "Workflow Product " + timestamp,
                "Testing workflow",
                1L,  // brandId
                2L,  // categoryId
                new BigDecimal("500.00"),
                "RON",
                new BigDecimal("19.00")
        );

        mockMvc.perform(post("/api/products/workflow/create-with-inventory")
                        .param("qty", "50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("WORKFLOW-SKU-" + timestamp))
                .andExpect(jsonPath("$.inventoryQuantityAvailable").value(50));
    }
}