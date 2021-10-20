package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void findAllShouldReturnSortedaPageWhenSortByName() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// Verificando se a requisição foi bem sucedida.
		result.andExpect(status().isOk());
		
		// Verificando se o total de elementos é igual ao total de registros já conhecido no banco de dados.
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		
		// Verificando se existe o elemento raiz no json de retorno da requisição.
		result.andExpect(jsonPath("$.content").exists());
		
		// Veriricando se os registros estão ordenados (funciona dessa forma apenas se os registros já forem conhecidos).
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		ProductDTO productDTO = Factory.createProductDTO();
		
		// O update necessita, além de um id, de um JSON no corpo da requisição que será o objeto atualizado.
		// Neste caso é necessário converter o objeto Java para JSON.
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		String expetedName = productDTO.getName();
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDosNotExists() throws Exception {
		
		// O update necessita, além de um id, de um JSON no corpo da requisição que será o objeto atualizado.
		// Neste caso é necessário converter o objeto Java para JSON.
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// Verificando se o status da requisição é "not found".
		result.andExpect(status().isNotFound());
	}
}