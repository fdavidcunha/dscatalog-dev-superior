package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired private MockMvc mockMvc;
	
	// Em controladores, geralmente se usa o @MockBean no lugar do @Mock.
	// Como o teste será unitário e não de integração (não preciso ir até o repositório testanto) então é necessário mockar o service.
	@MockBean private ProductService service;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		
		productDTO = Factory.createProductDTO();
		
		// List.of() -> Permite que seja intanciada uma lista com elementos dentro.
		page = new PageImpl<>(List.of(productDTO));
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
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
}