package com.devsuperior.dscatalog.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.CategoryDTO;
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
	
	// @PathVariable Long id -> indica para o spring que o parâmetro será a variável definida na rota "/{id}".
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
		
		CategoryDTO dto = service.findById(id);
		
		// Retornando a categoria no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(dto); 
	}
	
	// @RequestBody -> Para que o end-point reconheça o objeto enviado na requisição e "case" o objeto com o parâmetro do método insert.
	@PostMapping
	public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto) {
		dto = service.insert(dto);
		
		// Por padrão o ResponseEntity.ok() retorna o código 200. Porém, na inclusão de um novo registro, o código recomendado é o 201 (recurso criado).
		// Retorna no cabeçalho da resposta o endereço do recurso criado.
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		
		return ResponseEntity.created(uri).body(dto); 
	}
	
	// @RequestBody -> Para que o end-point reconheça o objeto enviado na requisição e "case" o objeto com o parâmetro do método insert.
	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
		dto = service.update(id, dto);
		
		// Retornando a categoria no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(dto); 
	}
	
	// @RequestBody -> Para que o end-point reconheça o objeto enviado na requisição e "case" o objeto com o parâmetro do método insert.
	// O retorno do delete também pode ser um ResponseEntity<Void>, já que não será necessário retornar um DTO no corpo da requisição.
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		
		// No delete a resposta não precisa ter corpo
		// ResponseEntity.noContent().build() -> Responde um código 204 - No content.
		return ResponseEntity.noContent().build(); 
	}
}
