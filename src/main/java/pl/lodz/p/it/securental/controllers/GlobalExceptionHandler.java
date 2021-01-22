package pl.lodz.p.it.securental.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        if (e instanceof ApplicationBaseException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApplicationBaseException.KEY_DEFAULT);
        }
    }
}
