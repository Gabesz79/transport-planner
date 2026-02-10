package hu.webuni.transport.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

//Minden Service-re, Controller-re érvényes (globális) hibakezelő, bárhol kivételt dob, egységes válaszforma adás történik itt:
@RestControllerAdvice
public class CustomExceptionHandler {
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<MyError> handleResponseStatusException(ResponseStatusException e) {
		HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value()); //StatusCode kinyerése (pl. 404)
		return ResponseEntity.status(status) //new ResponseStatusException(HttpStatus.NOT_FOUND) vagy HttpStatus.BAD_REQUEST esetén e.getReason() == null, vagyis nincs magyarázat, ekkor StatusCode=404 vagy 400 a válasz, egyéb esetben küldöm a megadott szöveget 
				.body(new MyError("ERROR", e.getReason() == null ? status.getReasonPhrase() : e.getReason()));
	}
	
	//DTO validáció elbukása esetén fut le (@Valid érvényesítése):
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MyError> handleValidation(MethodArgumentNotValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new MyError("VALIDATION_ERROR", "Validation failed")); //Könnyebb a hiba azonosítás String-ként 
	}
	
	
	
	
	
}
