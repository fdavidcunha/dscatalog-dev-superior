package com.devsuperior.dscatalog.services;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	// Autowired instancia automaticamente o objeto anotado, neste caso o repository.
	@Autowired private CategoryRepository repository;
	
	// @Transactional -> O próprio framework spring envolve o método em uma transação com o banco de dados.
	// readOnly = true evita que o banco de dados seja lockado, melhorando assim a performance.
	//                 Mais utilizado em métodos que fazem apenas leitura de dados.
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> list = repository.findAll(pageable);
		
		// .map() -> Transforma cada cada elemento original em outra coisa. Ela aplica uma função a cada elemento da lista.
		// x -> new CategoryDTO(x) -> Para cada elemento da lista chama a função CategoryDTO(x), que transforma o objeto Category em CategoryDTO. 
		return list.map(x -> new CategoryDTO(x)); 
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		// O objeto Optional surgiu a partir do Java 8 para evitar que se trabalhe com valor nulo.
		// O retorno desta busca nunca será um valor nulo.
		Optional<Category> obj = repository.findById(id);
		
		// Extraindo o objeto Category de dentro do Optional;
		// orElseThrow() -> Permite levantar uma exceção caso a recuperação do objeto falhe.
		Category entity = obj.orElseThrow( () -> new ResourceNotFoundException( "Objeto não encontrado!" ) );

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		
		// O "save", por padrão, retorna uma referência para a entidade salva. Por isso é necessário atualizar a variável local.
		entity = repository.save(entity);
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		
		try {
			// Neste caso usa-se o getOne() e não o findById().
			// O getOne() -> Não acessa o banco de dados. Ele apenas instancia um objeto provisório. Apenas quando salvar é que ele fará o acesso ao banco de dados.
			// Esse método deve ser usado para atualizar registros, evitando assim um acesso a mais ao banco, que seria realizado pelo findById().
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
			
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
}
