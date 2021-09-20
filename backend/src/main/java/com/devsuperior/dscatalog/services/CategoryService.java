package com.devsuperior.dscatalog.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public List<Category> findAll() {
		return repository.findAll();
	}
	
}
