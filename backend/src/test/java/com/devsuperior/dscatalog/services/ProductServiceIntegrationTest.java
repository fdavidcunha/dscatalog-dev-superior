package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTest {

	@Autowired private ProductService service;
	@Autowired private ProductRepository repository;
	
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
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(existingId);
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
	
		// Testando se existe pelo menos a primeira página, com 10 elementos.
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
	
		// Testando se a página 50 existe.
		PageRequest pageRequest = PageRequest.of(50, 10);
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPagedShouldReturnSortedePageWhenSortByName() {
	
		// Testando se a página está ordenada.
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		
		// Verificando se os itens estão ordenados (funciona dessa forma apenas quando se conhece os valores no banco de dados).
		Assertions.assertEquals("Macbook Pro"   , result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer"      , result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa" , result.getContent().get(2).getName());
	}
}