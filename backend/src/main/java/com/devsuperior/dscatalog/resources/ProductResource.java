package com.devsuperior.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;

/* Implementação do controlador REST, para a classe Product.                                                                                             */
/* Pontos importantes sobre esta classe:                                                                                                                  */
/*                                                                                                                                                        */
/* ResponseEntity<List<ProductDTO>> -> Retorna todos os registros do banco de dados, SEM paginação.                                                      */
/* ResponseEntity<Page<ProductDTO>> -> Retorna todos os registros do banco de dados, COM paginação.                                                      */
/* @GetMapping                       -> Informa que o método/end point será um serviço da API - responderá pelo verbo GET.                                */
/* @PostMapping                      -> Informa que o método/end point será um serviço da API - responderá pelo verbo POST.                               */
/* @PutMapping                       -> Informa que o método/end point será um serviço da API - responderá pelo verbo PUT.                                */
/* @@DeleteMapping                   -> Informa que o método/end point será um serviço da API - responderá pelo verbo DELETE.                             */
/* @PathVariable                     -> O parâmetro é obrigatório na URI. indica para o spring que o parâmetro será a variável definida na rota "/{id}".  */
/* @RequestParam                     -> O parâmetro é opcional. Indica para o spring que  o parâmetro pode ou não ser passado pela URI.                   */
/* @RequestBody                      -> Para que o end-point reconheça o objeto enviado na requisição e "case" o objeto com o parâmetro do método insert. */    

@RestController
@RequestMapping(value = "/products")
public class ProductResource {
	
	// Injetando uma dependência com a camada de serviço.
	@Autowired
	private ProductService service;
	
	// Endpoint para listar todas as categorias e encapsular uma resposta HTTP. 
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAll(
			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
			@RequestParam(value = "name", defaultValue = "") String name,
			Pageable pageable) {
		
		// Pode-se retornar a lista de duas formas:
		// List<ProductDTO> list -> Retorna todos os registros do banco de dados, sem paginação.
		//                          List é uma interface, por isso se inicializa a variável com ArrayList, que é uma das implementações da List.
		// Page<ProductDTO> list -> Retorna todos os registros do banco de dados, com paginação.
		Page<ProductDTO> list = service.findAllPaged(categoryId, name.trim(), pageable);
		
		// Retornando a lista de categorias no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(list); 
	}
	
	// Endpoint para recuperar uma categoria pelo ID e encapsular uma resposta HTTP. 
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
		
		ProductDTO dto = service.findById(id);
		
		// Retornando a categoria no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(dto); 
	}
	
	// Endpoint para inserir uma categoria e encapsular uma resposta HTTP.
	// @Valid -> indica que as anotações de validação (tamanho, requerido, etc), que estão nos atributos do DTO, serão validadas pelo spring.
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
		dto = service.insert(dto);
		
		// Por padrão o ResponseEntity.ok() retorna o código 200. Porém, na inclusão de um novo registro, o código recomendado é o 201 (recurso criado).
		// Retorna no cabeçalho da resposta o endereço do recurso criado.
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		
		return ResponseEntity.created(uri).body(dto); 
	}
	
	// Endpoint para alterar uma categoria e encapsular uma resposta HTTP. 
	// @Valid -> indica que as anotações de validação (tamanho, requerido, etc), que estão nos atributos do DTO, serão validadas pelo spring.
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
		dto = service.update(id, dto);
		
		// Retornando a categoria no corpo da resposta HTTP da requisição.
		// ResponseEntity.ok informa que o código de retorno é 200 - Sucesso.
		return ResponseEntity.ok().body(dto); 
	}
	
	// Endpoint para excluir uma categoria. 
	// O retorno do delete também pode ser um ResponseEntity<Void>, já que não será necessário retornar um DTO no corpo da requisição.
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		
		// No delete a resposta não precisa ter corpo
		// ResponseEntity.noContent().build() -> Responde um código 204 - No content.
		return ResponseEntity.noContent().build(); 
	}
}