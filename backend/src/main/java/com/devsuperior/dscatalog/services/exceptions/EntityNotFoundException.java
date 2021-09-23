package com.devsuperior.dscatalog.services.exceptions;

// Se a classe herdar de "Exception" ela vai ter que obrigatoriamente ser tratada.
// O compilador não vai deixar não tratar com try catch ou então não propagar ela para o método que a chamou.
// Para se ter uma exceção mais flexível, que pode-se tratar ou não, é necessário herdar de RuntimeException.
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public EntityNotFoundException(String msg) {
		super(msg);
	}
}
