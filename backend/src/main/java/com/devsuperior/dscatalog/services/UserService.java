package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {

	// Autowired instancia automaticamente o objeto anotado, neste caso o repository.
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	// @Transactional -> O próprio framework spring envolve o método em uma transação com o banco de dados.
	// readOnly = true evita que o banco de dados seja lockado, melhorando assim a performance.
	//                 Mais utilizado em métodos que fazem apenas leitura de dados.
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = userRepository.findAll(pageable);
		
		// .map() -> Transforma cada cada elemento original em outra coisa. Ela aplica uma função a cada elemento da lista.
		// x -> new UserDTO(x) -> Para cada elemento da lista chama a função UserDTO(x), que transforma o objeto User em UserDTO. 
		return list.map(x -> new UserDTO(x)); 
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		
		// O objeto Optional surgiu a partir do Java 8 para evitar que se trabalhe com valor nulo.
		// O retorno desta busca nunca será um valor nulo.
		Optional<User> obj = userRepository.findById(id);
		
		// Extraindo o objeto User de dentro do Optional;
		// orElseThrow() -> Permite levantar uma exceção caso a recuperação do objeto falhe.
		User entity = obj.orElseThrow( () -> new ResourceNotFoundException( "Objeto não encontrado!" ) );

		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDTOToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));

		// O "save", por padrão, retorna uma referência para a entidade salva. Por isso é necessário atualizar a variável local.
		entity = userRepository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		
		try {
			// Neste caso usa-se o getOne() e não o findById().
			// O getOne() -> Não acessa o banco de dados. Ele apenas instancia um objeto provisório. Apenas quando salvar é que ele fará o acesso ao banco de dados.
			// Esse método deve ser usado para atualizar registros, evitando assim um acesso a mais ao banco, que seria realizado pelo findById().
			User entity = userRepository.getOne(id);
			copyDTOToEntity(dto, entity);
			entity = userRepository.save(entity);
			return new UserDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID não encontrado: " + id);
		}
	}

	// O delete não tem o @Transactional, pois ela impede a captura de algumas exceções, como a EmptyResultDataAccessException e a DataIntegrityViolationException.  
	public void delete(Long id) {
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("ID não encontrado: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade");
		}
	}
	
	private void copyDTOToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		for (RoleDTO roleDTO : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDTO.getId());
			entity.getRoles().add(role);
		}
	}
}