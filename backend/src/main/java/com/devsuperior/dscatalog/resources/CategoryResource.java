package com.devsuperior.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.services.CategoryService;

/* Implementação do controlador REST, para a classe Category. */

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	
	// Injetando uma dependência com a camada de serviço.
	@Autowired
	private CategoryService service;
	
	// Endpoint para listar todas as categorias e encapsular uma resposta HTTP. 
	// @GetMapping informa que o método/end point será um serviço da API.
	
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAll() {
		
		// List é uma interface, por isso se inicializa a variável com ArrayList, que é uma das implementações da List.
		List<CategoryDTO> list = service.findAll();
		
		// Retornando a lista de categorias no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(list); 
	}
}
