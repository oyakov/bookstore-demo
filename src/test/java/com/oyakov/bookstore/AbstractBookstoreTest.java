package com.oyakov.bookstore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ActiveProfiles({ "test" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@SpringBootTest
@AutoConfigureWebTestClient(timeout = "500000")
public class AbstractBookstoreTest extends AbstractTestNGSpringContextTests {
}
