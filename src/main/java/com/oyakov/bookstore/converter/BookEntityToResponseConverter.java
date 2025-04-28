package com.oyakov.bookstore.converter;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.enums.BookType;
import com.oyakov.bookstore.model.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.oyakov.bookstore.model.TypeEnum;

@Component
@RequiredArgsConstructor
public class BookEntityToResponseConverter implements Converter<BookEntity, BookResponse> {

    @Override
    public BookResponse convert(BookEntity source) {
        BookResponse response = new BookResponse();
        response.setId(source.getId());
        response.setTitle(source.getTitle());
        response.setAuthor(source.getAuthor());
        response.setBasePrice(source.getBasePrice());
        response.setQuantity(source.getQuantity());

        // Use ConversionService for enum mapping
        if (source.getType() != null) {
            response.setType(convertType(source.getType()));
        }

        return response;
    }

    private TypeEnum convertType(BookType entityType) {
        return TypeEnum.valueOf(entityType.name());
    }
}
