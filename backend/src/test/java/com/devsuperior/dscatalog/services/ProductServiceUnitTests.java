package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	// Quando se usa um Mock é obrigatório configurar o comportamento simulado dele.
	// Por exemplo, se o service vai chamar o delete, deve-se configurar o comportamento do delete do repositório.
	@Mock private ProductRepository productRepository;
	@Mock private CategoryRepository categoryRepository; 
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page; // PageImpl -> É utilizado nos testes para representar uma página.
	private Product product;
	private ProductDTO productDTO;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingId    = 1L;
		nonExistingId = 1000L;
		dependentId   = 4;
		
		product    = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category   = Factory.createCategory();
		page       = new PageImpl<>(List.of(product));
		
		/* Abaixo os métodos que não retornam alguma coisa */

		// Quando chamar o findAll, passando qualquer valor (ArgumentMatchers.any()), faça alguma coisa, que nesse caso é retornar um page, com uma lista de produtos. (thenReturn(page)).
		Mockito.when(productRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		// Quando chamar o save, deve retornar um produto.
		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		
		// Quando chamar o findById com um ID existente, deve retornar um Optional com um produto dentro.
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
		
		// Quando chamar o findById com um ID inexistente, deve retornar um Optional vazio.
		Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		// Quando chamar o getOne com um ID existente, deve retornar um Product.
		Mockito.when(productRepository.getOne(existingId)).thenReturn(product);

		// Quando chamar o getOne com um ID inexistente, deve retornar um EntityNotFoundException.
		Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		// Quando chamar o getOne com um ID existente, deve retornar um Product.
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);

		// Quando chamar o getOne com um ID inexistente, deve retornar um EntityNotFoundException.
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		/* Abaixo os métodos que não retornam nada */		
		
		// Configurando o comportamento simulado do método deleteById, usando o Mockito.
		// Mockito.doNothing() -> Neste caso usa-se o doNothing pois o método deleteById não retorna nada.
		// Traduzindo: Quando se chamar o deleteById, com ID existente, o método não vai levantar exceção nem vai retornar nada.
		Mockito.doNothing().when(productRepository).deleteById(existingId);
		
		// Mockito.doThrow() -> Neste caso usa-se o doThrow pois o método deleteById retorna uma exceção quando o ID a ser deletado não existe.
		// Traduzindo: Quando se chamar o deleteById, com ID inexistente, o método vai levantar a exceção EmptyResultDataAccessException.
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
	}
	
	@Test
	public void updateSouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			// Como os dados foram mockados acima, o service não vai usar o findById do repositório e sim do mockito.
			service.update(nonExistingId, productDTO);
		});
	}

	@Test
	public void updateSouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.update(existingId, productDTO);
		Assertions.assertNotNull(result);
	}

	@Test
	public void findByIdSouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			// Como os dados foram mockados acima, o service não vai usar o findById do repositório e sim do mockito.
			service.findById(nonExistingId);
		});
	}

	@Test
	public void findByIdSouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageable);
		Assertions.assertNotNull(result);
		Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenExistDependenceId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			// Como os dados foram mockados acima, o service não vai usar o delete do repositório e sim do mockito.
			service.delete(dependentId);
		});
		
		// Verificando se o método deleteById foi chamado na ação do teste acima.
		// Funciona como um Assertions.
		// Mockito.times(X) -> Serve para definir a quantidade de vezes que se espera que o método seja chamado. 
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			// Como os dados foram mockados acima, o service não vai usar o delete do repositório e sim do mockito.
			service.delete(nonExistingId);
		});
		
		// Verificando se o método deleteById foi chamado na ação do teste acima.
		// Funciona como um Assertions.
		// Mockito.times(X) -> Serve para definir a quantidade de vezes que se espera que o método seja chamado. 
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			// Como os dados foram mockados acima, o service não vai usar o delete do repositório e sim do mockito.
			service.delete(existingId);
		});
		
		// Verificando se o método deleteById foi chamado na ação do teste acima.
		// Funciona como um Assertions.
		// Mockito.times(X) -> Serve para definir a quantidade de vezes que se espera que o método seja chamado. 
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
	}
}