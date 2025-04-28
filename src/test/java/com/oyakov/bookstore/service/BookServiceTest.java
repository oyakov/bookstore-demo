package com.oyakov.bookstore.service;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.enums.BookType;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.repository.BookRepository;
import com.oyakov.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    void getBookById_shouldReturnBook() {
        BookEntity bookEntity = new BookEntity(1L, "Title", "Author", BookType.REGULAR, BigDecimal.TEN, 10, null, null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));

        BookEntity result = bookService.getBookById(1L);

        assertEquals("Title", result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_shouldThrowNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void createBook_shouldSaveBook() {
        // Arrange
        BookEntity inputBookEntity = new BookEntity(null, "New Book", "Author", BookType.NEW_RELEASE, BigDecimal.valueOf(20), 10, null, null);
        BookEntity savedBookEntity = new BookEntity(1L, "New Book", "Author", BookType.NEW_RELEASE, BigDecimal.valueOf(20), 10, null, null);

        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedBookEntity);

        // Act
        BookEntity result = bookService.createBook(inputBookEntity);

        // Assert
        assertNotNull(result.getId());

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());
        BookEntity capturedBookEntity = captor.getValue();

        assertEquals("New Book", capturedBookEntity.getTitle());
        assertNotNull(capturedBookEntity.getCreatedAt());
        assertNotNull(capturedBookEntity.getUpdatedAt());
    }


    @Test
    void getAllBooks_shouldReturnBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(new BookEntity(), new BookEntity()));

        assertEquals(2, bookService.getAllBooks().size());
        verify(bookRepository).findAll();
    }

    @Test
    void updateBook_shouldUpdateFields() {
        // Arrange
        BookEntity existing = new BookEntity(1L, "Old Title", "Old Author", BookType.REGULAR, BigDecimal.TEN, 5, null, null);
        BookEntity updated = new BookEntity(null, "New Title", "New Author", BookType.NEW_RELEASE, BigDecimal.valueOf(20), 10, null, null);

        when(bookRepository.lockBookById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookEntity result = bookService.updateBook(1L, updated);

        // Assert
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(BookType.NEW_RELEASE, result.getType());
        assertEquals(BigDecimal.valueOf(20), result.getBasePrice());
        assertEquals(10, result.getQuantity());
        assertNotNull(result.getUpdatedAt());

        // Captor to verify the saved book
        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());
        BookEntity captured = captor.getValue();
        assertEquals("New Title", captured.getTitle());
        assertNotNull(captured.getUpdatedAt());
    }

    @Test
    void updateBook_shouldThrowIfNotFound() {
        // Arrange
        BookEntity updated = new BookEntity(null, "New Title", "New Author", BookType.NEW_RELEASE, BigDecimal.valueOf(20), 10, null, null);
        when(bookRepository.lockBookById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookService.updateBook(1L, updated));
    }
}

