package com.example.rag.exception;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
    ResponseEntity<Map<String, String>> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() == null ? "Request failed" : e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> internal(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "The request could not be completed. Check server logs for details."));
    }
}
