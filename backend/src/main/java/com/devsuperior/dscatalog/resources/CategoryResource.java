package com.devsuperior.dscatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.entities.Category;

/* Implementação do controlador REST, para a classe Category. */

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	
	// Endpoint para listar todas as categorias e encapsular uma resposta HTTP. 
	// @GetMapping informa que o método/end point será um serviço da API.
	
	@GetMapping
	public ResponseEntity<List<Category>> findAll() {
		
		// List é uma interface, por isso se inicializa a variável com ArrayList, que é uma das implementações da List.
		List<Category> list = new ArrayList<>();
		list.add(new Category(1L, "Livros"));
		list.add(new Category(2L, "Eletrônicos"));
		
		// Retornando a lista de categorias no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(list); 
	}
}
