package com.oyakov.bookstore;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.enums.BookType;
import com.oyakov.bookstore.repository.BookRepository;
import com.oyakov.bookstore.service.BookService;
import com.oyakov.bookstore.service.PurchaseService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
class ConcurrencyTest extends AbstractBookstoreTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testPessimisticLockingOnStockUpdate() throws Exception {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("Concurrency Book");
        bookEntity.setAuthor("Author");
        bookEntity.setType(BookType.REGULAR);
        bookEntity.setBasePrice(BigDecimal.TEN);
        bookEntity.setQuantity(5);
        bookRepository.saveAndFlush(bookEntity);

        Long bookId = bookEntity.getId();
        Long customerId = 1L;

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch latch = new CountDownLatch(1);

            // Thread 1: Admin updates stock (locks the row)
            Future<?> adminTask = executor.submit(() -> {
                BookEntity existing = bookService.getBookById(bookId);
                existing.setQuantity(10);
                latch.countDown(); // Allow purchase thread to start
                try {
                    Thread.sleep(3000); // Hold the lock for a while
                } catch (InterruptedException ignored) {}
                bookService.updateBook(bookId, existing); // Triggers locking
            });

            // Thread 2: Customer tries to purchase
            Future<?> purchaseTask = executor.submit(() -> {
                try {
                    latch.await(); // Wait until admin starts
                    List<Long> bookIds = List.of(bookId);
                    purchaseService.purchaseBooks(customerId, bookIds); // Should wait for admin lock to release
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            adminTask.get();
            purchaseTask.get();
        }

    }
}

