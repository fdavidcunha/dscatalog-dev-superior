package com.devsuperior.dscatalog.services;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	// Autowired instancia automaticamente o objeto anotado, neste caso o repository.
	@Autowired private ProductRepository repository;
	@Autowired private CategoryRepository categoryRepository;
	
	// @Transactional -> O próprio framework spring envolve o método em uma transação com o banco de dados.
	// readOnly = true evita que o banco de dados seja lockado, melhorando assim a performance.
	//                 Mais utilizado em métodos que fazem apenas leitura de dados.
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> list = repository.findAll(pageRequest);
		
		// .map() -> Transforma cada cada elemento original em outra coisa. Ela aplica uma função a cada elemento da lista.
		// x -> new ProductDTO(x) -> Para cada elemento da lista chama a função ProductDTO(x), que transforma o objeto Product em ProductDTO. 
		return list.map(x -> new ProductDTO(x)); 
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		// O objeto Optional surgiu a partir do Java 8 para evitar que se trabalhe com valor nulo.
		// O retorno desta busca nunca será um valor nulo.
		Optional<Product> obj = repository.findById(id);
		
		// Extraindo o objeto Product de dentro do Optional;
		// orElseThrow() -> Permite levantar uma exceção caso a recuperação do objeto falhe.
		Product entity = obj.orElseThrow( () -> new ResourceNotFoundException( "Objeto não encontrado!" ) );

		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDTOToEntity(dto, entity);
		// O "save", por padrão, retorna uma referência para a entidade salva. Por isso é necessário atualizar a variável local.
		entity = repository.save(entity);
		
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		
		try {
			// Neste caso usa-se o getOne() e não o findById().
			// O getOne() -> Não acessa o banco de dados. Ele apenas instancia um objeto provisório. Apenas quando salvar é que ele fará o acesso ao banco de dados.
			// Esse método deve ser usado para atualizar registros, evitando assim um acesso a mais ao banco, que seria realizado pelo findById().
			Product entity = repository.getOne(id);
			copyDTOToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
			
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID não encontrado: " + id);
		}
	}

	// O delete não tem o @Transactional, pois ela impede a captura de algumas exceções, como a EmptyResultDataAccessException e a DataIntegrityViolationException.  
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("ID não encontrado: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade");
		}
	}
	
	private void copyDTOToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for (CategoryDTO catDTO : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDTO.getId());
			entity.getCategories().add(category);
		}
	}
}
