package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

// Parâmetros: UserInsertValid -> Tipo da anotation.
//             UserInsertDTO   -> Tipo da classe que vai receber a anotation. Essa anotation estará na classe UserInsertDTO.            
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

	@Autowired private UserRepository userRepository;
	
	@Override
	public void initialize(UserInsertValid ann) {
		// Lógicas de inicialização de objetos são definidas aqui.
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		User user = userRepository.findByEmail(dto.getEmail());
		if (user != null) {
			list.add(new FieldMessage("email", "E-mail já cadastrado."));
		}
		
		// Se houver algum erro, esses erros são adicionados ao bean validations das anotações
		// padrão, que foram especificadas nas classes DTO.
		// Por exemplo: Se o atributo "nome" de um DTO não foi informado isso irá gerar um ERRO que será
		// devolvido para o usuário através da exception MethodArgumentNotValidException
		// Como foi criada uma anotação personalizada (UserInsertValidator) todas os erros de validação aqui definidos
		// Também precisam ser retornados via MethodArgumentNotValidException.
		// O código abaixo está percorrendo todos os erros encontrados aqui e retornando na lista de MethodArgumentNotValidException.

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage())
				.addPropertyNode(e.getFieldName())
				.addConstraintViolation();
		}
		return list.isEmpty();
	}
}