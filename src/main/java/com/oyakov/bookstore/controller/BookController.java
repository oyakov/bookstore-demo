package com.oyakov.bookstore.controller;

import com.oyakov.bookstore.api.BooksApi;
import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.exception.DuplicateEntityException;
import com.oyakov.bookstore.model.BookRequest;
import com.oyakov.bookstore.model.BookResponse;
import com.oyakov.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController implements BooksApi {

    private final BookService bookService;
    private final ConversionService conversionService;

    @Override
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookEntity> entities = bookService.getAllBooks();
        List<BookResponse> responses = entities.stream()
                .map(entity -> conversionService.convert(entity, BookResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        BookEntity entity = bookService.getBookById(id);
        BookResponse response = conversionService.convert(entity, BookResponse.class);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
        BookEntity entity = conversionService.convert(request, BookEntity.class);
        BookEntity created = bookService.createBook(entity);
        BookResponse response = conversionService.convert(created, BookResponse.class);
        return ResponseEntity.created(URI.create("/books/%d".formatted(response.getId())))
                .body(response);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<String> handleDuplicateEntityException(DuplicateEntityException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Book error: %s".formatted(ex.getMessage()));
    }

    @Override
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        BookEntity entity = conversionService.convert(request, BookEntity.class);
        BookEntity updated = bookService.updateBook(id, entity);
        BookResponse response = conversionService.convert(updated, BookResponse.class);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

