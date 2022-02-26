package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceUnitTests {

	@Autowired private MockMvc mockMvc;
	
	// Em controladores, geralmente se usa o @MockBean no lugar do @Mock.
	// Como o teste será unitário e não de integração (não preciso ir até o repositório testanto) então é necessário mockar o service.
	@MockBean private ProductService service;
	
	@Autowired private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingId    = 1L;
		nonExistingId = 2L;
		dependentId   = 3L;
		
		productDTO = Factory.createProductDTO();
		
		// List.of() -> Permite que seja intanciada uma lista com elementos dentro.
		page = new PageImpl<>(List.of(productDTO));
		
		//Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(existingId)).thenReturn(productDTO);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		Mockito.when(service.update(Mockito.eq(existingId), Mockito.any())).thenReturn(productDTO);
		Mockito.when(service.update(Mockito.eq(nonExistingId), Mockito.any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);

		Mockito.when(service.insert(Mockito.any())).thenReturn(productDTO);
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesExists() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}

	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception {
		
		// O update necessita, além de um id, de um JSON no corpo da requisição que será o objeto atualizado.
		// Neste caso é necessário converter o objeto Java para JSON.
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(post("/products", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		// O update necessita, além de um id, de um JSON no corpo da requisição que será o objeto atualizado.
		// Neste caso é necessário converter o objeto Java para JSON.
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
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

	@Test
	public void findAllShouldReturnPage() throws Exception {
		
		// mockMvc.perform -> Faz uma requisição.
		mockMvc.perform(get("/products")).andExpect(status().isOk());
		
		// Outro modo mais verboso de fazer a requisição acima:
		// ResultActions result = mockMvc.perform(get("/products"));
		// result.andExpect(status().isOk());
		
		// Fazendo a requisição e especificando a media type:
		// ResultActions result = 
		// mockMvc.perform(get("/products")
		//     .accept(MediaType.APPLICATION_JSON));
		// result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// Verificando se o status da requisição é 200.
		result.andExpect(status().isOk());
		
		// Verificando se foi retornado um JSON de produtos. 
		// O $ acessa o objeto JSON da resposta.
		// "$.id" -> Verifica se existe um campo "id" no JSON retornado, e assim se dá para os demais campos abaixo.
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// Verificando se o status da requisição é "not found".
		result.andExpect(status().isNotFound());
	}
}