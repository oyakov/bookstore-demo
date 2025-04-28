package com.oyakov.bookstore.service.impl;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.exception.DuplicateEntityException;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.repository.BookRepository;
import com.oyakov.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public BookEntity getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Book not found: %d", id)));
    }

    @Override
    @Transactional
    public BookEntity createBook(BookEntity bookEntity) {
        bookEntity.setCreatedAt(LocalDateTime.now());
        bookEntity.setUpdatedAt(LocalDateTime.now());
        try {
            return bookRepository.save(bookEntity);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
                if ("unique_book_constraint".equals(constraintEx.getConstraintName())) {
                    throw new DuplicateEntityException("Book with the same title, author, and type already exists.");
                }
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public BookEntity updateBook(Long id, BookEntity updatedBookEntity) {
        // Use pessimistic lock
        BookEntity existing = bookRepository.lockBookById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Book not found: %s", id)));

        existing.setTitle(updatedBookEntity.getTitle());
        existing.setAuthor(updatedBookEntity.getAuthor());
        existing.setType(updatedBookEntity.getType());
        existing.setBasePrice(updatedBookEntity.getBasePrice());
        existing.setQuantity(updatedBookEntity.getQuantity()); // include quantity update
        existing.setUpdatedAt(LocalDateTime.now());

        return bookRepository.save(existing);
    }


    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }
}

