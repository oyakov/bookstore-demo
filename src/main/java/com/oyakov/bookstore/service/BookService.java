package com.oyakov.bookstore.service;

import com.oyakov.bookstore.entity.BookEntity;
import java.util.List;

public interface BookService {
    List<BookEntity> getAllBooks();
    BookEntity getBookById(Long id);
    BookEntity createBook(BookEntity bookEntity);
    BookEntity updateBook(Long id, BookEntity bookEntity);
    void deleteBook(Long id);
}

