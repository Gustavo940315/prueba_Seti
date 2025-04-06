package com.seti.ms.franchises.config.exception;

import com.seti.ms.franchises.config.ExceptionConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionConfigs exceptionConfigs;

    public GlobalExceptionHandler(ExceptionConfigs exceptionConfigs) {
        this.exceptionConfigs = exceptionConfigs;
    }

    @ExceptionHandler(MyHandleException.class)
    public Mono<ResponseEntity<String>> handleCustomException(MyHandleException ex) {
        var msg = exceptionConfigs.getException(ExceptionConfigs.PERZONALIZADA) + " : " + ex.getMessage();
        log.error(msg);
        return Mono.just(ResponseEntity.badRequest().body(msg));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGeneralException(Exception ex) {
        var msg = exceptionConfigs.getException(ExceptionConfigs.SYSYEM) + " : " + ex.getMessage();
        log.error(msg);
        return Mono.just(ResponseEntity.badRequest().body(msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<List<String>>> handleValidationException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "Validación: %s [%s: %s]".formatted(
                        error.getObjectName(), error.getField(), error.getDefaultMessage()))
                .toList();

        return Mono.just(ResponseEntity.badRequest().body(errors));
    }
}
