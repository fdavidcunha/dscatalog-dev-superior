package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;

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
}
