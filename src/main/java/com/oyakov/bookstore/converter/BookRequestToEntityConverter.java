package com.oyakov.bookstore.converter;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.enums.BookType;
import com.oyakov.bookstore.model.BookRequest;
import com.oyakov.bookstore.model.TypeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookRequestToEntityConverter implements Converter<BookRequest, BookEntity> {

    public BookRequestToEntityConverter() {
    }

    @Override
    public BookEntity convert(BookRequest source) {
        BookEntity entity = new BookEntity();
        entity.setTitle(source.getTitle());
        entity.setAuthor(source.getAuthor());
        entity.setBasePrice(source.getBasePrice());
        entity.setQuantity(source.getQuantity());

        if (source.getType() != null) {
            entity.setType(convertType(source.getType()));
        }

        return entity;
    }

    private BookType convertType(TypeEnum dtoType) {
        return BookType.valueOf(dtoType.name());
    }
}
