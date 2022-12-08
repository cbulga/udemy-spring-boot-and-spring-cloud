package com.xantrix.webapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorResponse> exceptionNotFoundHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errore = ErrorResponse.builder()
                .codice(HttpStatus.NOT_FOUND.value())
                .messaggio(ex.getMessage())
                .build();
        return new ResponseEntity<>(errore, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindingException.class)
    public ResponseEntity<ErrorResponse> exceptionBindingHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errore = ErrorResponse.builder()
                .codice(HttpStatus.BAD_REQUEST.value())
                .messaggio(ex.getMessage())
                .build();
        return new ResponseEntity<>(errore, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> exceptionDeplicateRecordHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errore = ErrorResponse.builder()
                .codice(HttpStatus.NOT_ACCEPTABLE.value())
                .messaggio(ex.getMessage())
                .build();
        return new ResponseEntity<>(errore, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errore = ErrorResponse.builder()
                .codice(HttpStatus.BAD_REQUEST.value())
                .messaggio("La richiesta non può essere eseguita a causa di un errore generico")
                .build();
        return new ResponseEntity<>(errore, HttpStatus.BAD_REQUEST);
    }
}
