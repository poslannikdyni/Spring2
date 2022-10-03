package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.web.request.BookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(componentModel = "spring")
public interface BookMapper {

    default Book resultSetToBook(ResultSet rs) throws SQLException {
          return new Book(
                  rs.getLong("ID"),
                  rs.getLong("USER_ID"),
                  rs.getString("TITLE"),
                  rs.getString("AUTHOR"),
                  rs.getLong("PAGE_COUNT")
                  );
    }

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    BookRequest bookDtoToBookRequest(BookDto bookDto);

    Book bookDtoToBook(BookDto bookDto);

    BookDto bookToBookDto(Book book);

    void updateBookFromDto(BookDto dto, @MappingTarget Book entity);

}
