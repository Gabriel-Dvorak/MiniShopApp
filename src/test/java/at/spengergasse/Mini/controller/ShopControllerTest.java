package at.spengergasse.Mini.controller;

import at.spengergasse.Mini.model.Shop;
import at.spengergasse.Mini.viewmodel.ShopRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndGetShop() throws Exception {
        ShopRequest request = new ShopRequest("TestShop");

        // Shop erstellen
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopName").value("TestShop"));

        // Shop abrufen
        mockMvc.perform(get("/api/shop/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopName").value("TestShop"));
    }

    @Test
    void getShopById() {
        Shop shop = restTemplate.getForObject("/api/shop/1", Shop.class);
        assertNotNull(shop);
    }
}
