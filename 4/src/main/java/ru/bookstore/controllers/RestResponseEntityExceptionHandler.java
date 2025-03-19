package ru.bookstore.controllers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.bookstore.exceptions.DataAccessException;
import ru.bookstore.exceptions.EntityNotFoundException;
import ru.bookstore.exceptions.ExportException;
import ru.bookstore.exceptions.ImportException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
    log.error("Сущность не найдена: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Сущность не найдена");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DataAccessException.class)
  protected ResponseEntity<ProblemDetail> handleDataAccessException(DataAccessException ex, WebRequest request) {
    log.error("Ошибка доступа к данным: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problemDetail.setTitle("Ошибка доступа к данным");
    problemDetail.setProperty("timestamp", Instant.now());

    return new ResponseEntity<>(problemDetail, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    log.error("Некорректный аргумент: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Некорректный аргумент");
    problemDetail.setProperty("timestamp", Instant.now());

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    log.error("Ошибка валидации: {}", ex.getMessage(), ex);

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Ошибка валидации");
    problemDetail.setProperty("errors", errors);
    problemDetail.setProperty("timestamp", Instant.now());

    return new ResponseEntity<>(problemDetail, headers, status);
  }

  @ExceptionHandler(ImportException.class)
  protected ResponseEntity<ProblemDetail> handleImportException(ImportException ex, WebRequest request) {
    log.error("Ошибка импорта: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Ошибка импорта");
    problemDetail.setProperty("timestamp", Instant.now());

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExportException.class)
  protected ResponseEntity<ProblemDetail> handleExportException(ExportException ex, WebRequest request) {
    log.error("Ошибка экспорта: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    problemDetail.setTitle("Ошибка экспорта");
    problemDetail.setProperty("timestamp", Instant.now());

    return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ProblemDetail> handleGenericException(Exception ex, WebRequest request) {
    log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    problemDetail.setTitle("Внутренняя ошибка");
    problemDetail.setProperty("timestamp", Instant.now());

    return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
