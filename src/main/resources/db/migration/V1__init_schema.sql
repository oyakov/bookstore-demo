CREATE TABLE customers (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          loyalty_points INTEGER NOT NULL DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      type VARCHAR(50) NOT NULL,
                      base_price NUMERIC(15, 2) NOT NULL,
                      quantity INTEGER NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE books
    ADD CONSTRAINT unique_book_constraint
        UNIQUE (title, author, type);

CREATE TABLE purchase_orders (
                                id BIGSERIAL PRIMARY KEY,
                                customer_id BIGINT NOT NULL,
                                total_price NUMERIC(15, 2) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE purchase_orders_books (
                                      purchase_order_id BIGINT NOT NULL,
                                      book_id BIGINT NOT NULL,
                                      quantity INTEGER NOT NULL,
                                      PRIMARY KEY (purchase_order_id, book_id),
                                      FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
                                      FOREIGN KEY (book_id) REFERENCES books(id)
);
