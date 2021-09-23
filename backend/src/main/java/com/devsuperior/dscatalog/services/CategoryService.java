package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {

	// Autowired instancia automaticamente o objeto anotado, neste caso o repository.
	@Autowired private CategoryRepository repository;
	
	// O próprio framework spring envolve o método em uma transação com o banco de dados.
	// readOnly = true evita que o banco de dados seja lockado, melhorando assim a performance.
	//                 Mais utilizado em métodos que fazem apenas leitura de dados.
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		
		// list.stream() -> O stream é um recurso do Java 8+ que permite trabalhar com funções de alta ordem
		// incluindo funções lambda. Dá a possibilidade de fazer transformações na lista.
		// .map() -> Transforma cada cada elemento original em outra coisa. Ela aplica uma função a cada elemento da lista.
		// x -> new CategoryDTO(x) -> Para cada elemento da lista chama a função CategoryDTO(x), que transforma o objeto Category em CategoryDTO. 
		// .collect() -> Converte o stream em uma lista novamente.
		List<CategoryDTO> listDTO = list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		return listDTO; 
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		// O objeto Optional surgiu a partir do Java 8 para evitar que se trabalhe com valor nulo.
		// O retorno desta busca nunca será um valor nulo.
		Optional<Category> obj = repository.findById(id);
		
		// Extraindo o objeto Category de dentro do Optional;
		// orElseThrow() -> Permite levantar uma exceção caso a recuperação do objeto falhe.
		Category entity = obj.orElseThrow( () -> new EntityNotFoundException( "Objeto não encontrado!" ) );

		return new CategoryDTO(entity);
	}

	@Transactional(readOnly = true)
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getNameString());
		
		// O "save", por padrão, retorna uma referência para a entidade salva. Por isso é necessário atualizar a variável local.
		entity = repository.save(entity);
		
		return new CategoryDTO(entity);
	}
}
