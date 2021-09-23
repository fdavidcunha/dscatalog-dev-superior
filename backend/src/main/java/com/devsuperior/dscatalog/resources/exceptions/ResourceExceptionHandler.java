package com.devsuperior.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

// Essa classe serve para não ter que escrever um try..catch em todos os métodos do controlador que precisar tratar uma exceção.
// Ela irá interceptar as exceções que ocorrem no controlador e vai dar o tratamento adequado para ela.
// @ControllerAdvice -> Permite que a classe intercept alguma exceção que ocorrer na camada de resource (controlador Rest) e trata a exceção.

@ControllerAdvice
public class ResourceExceptionHandler {

	// HttpServletRequest -> Contém todas as informações da requisição.
	// @ExceptionHandler  -> Define que irá interceptar uma exceção. O parâmetro é o tipo de exceção que deverá ser interceptada.
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound( EntityNotFoundException e, HttpServletRequest request ) {
		
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(HttpStatus.NOT_FOUND.value());
		err.setError("Recurso não encontrado");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}
}
