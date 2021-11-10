package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

// Parâmetros: UserUpdateValid -> Tipo da anotation.
//             UserUpdateDTO   -> Tipo da classe que vai receber a anotation. Essa anotation estará na classe UserInsertDTO.            
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired private UserRepository userRepository;
	@Autowired private HttpServletRequest request;
	
	@Override
	public void initialize(UserUpdateValid ann) {
		// Lógicas de inicialização de objetos são definidas aqui.
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		// HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE -> Obtem um dicionário com os atributos da URL; 
		@SuppressWarnings("unchecked")
		var URIVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		// Obtendo informações da request.
		long userID = Long.parseLong(URIVars.get("id"));
		
		
		List<FieldMessage> list = new ArrayList<>();
		
		User user = userRepository.findByEmail(dto.getEmail());
		if (user != null && userID != user.getId()) {
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
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}