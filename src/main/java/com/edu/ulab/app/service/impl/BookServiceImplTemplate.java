package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.utility.ExceptionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;
    private final ExceptionUtility exceptionUtility;


    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper, ExceptionUtility exceptionUtility) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
        this.exceptionUtility = exceptionUtility;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        exceptionUtility.throwServiceExceptionIfNull(bookDto, "Update book failed : bookDto is null");
        exceptionUtility.throwServiceExceptionIfNull(bookDto.getId(), "Update book failed : bookDto.getId() returns null");

        if (getBookById(bookDto.getId()) == null) {
            exceptionUtility.throwNotFoundException(String.format("Update book failed : book with id = %s not exist", bookDto.getId()));
        }

        final String UPDATE_SQL = "UPDATE BOOK SET AUTHOR = ?, PAGE_COUNT = ?, TITLE = ?, USER_ID = ? WHERE ID = ?";
        Book book = bookMapper.bookDtoToBook(bookDto);
        int countRowHasChanged = 0;
        try {
            countRowHasChanged = jdbcTemplate.update(UPDATE_SQL,
                    book.getAuthor(),
                    book.getPageCount(),
                    book.getTitle(),
                    book.getUserId(),
                    book.getId()
            );
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Update book with id = %s failed", book.getId()));
        }
        if (countRowHasChanged == 0) {
            exceptionUtility.throwServiceException(String.format("Update book failed. Database has zero row with id = %s", book.getId()));
        } else if (countRowHasChanged > 1) {
            exceptionUtility.throwServiceException(String.format("Update book potentially unsafe. Database has %s row with id = %s", countRowHasChanged, book.getId()));
        }
        log.info("Update book successfully {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public BookDto getBookById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Get book failed : id is null");

        Book book = null;
        try {
            final String GET_BOOK_BY_ID_SQL = "SELECT * FROM BOOK WHERE ID=? LIMIT 1";
            book = jdbcTemplate.queryForObject(
                    GET_BOOK_BY_ID_SQL,
                    (rs, rowNum) -> bookMapper.resultSetToBook(rs),
                    id
            );

        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Get book  with id = %s failed", id));
        }

        exceptionUtility.throwNotFoundExceptionIfNull(book, String.format("Get book failed : book with id = %s not exist", id));
        log.info("Get book successfully {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Delete book failed : id is null");

        try {
            final String DELETE_BOOK_BY_ID_SQL = "DELETE FROM BOOK WHERE ID = ?";
            jdbcTemplate.update(DELETE_BOOK_BY_ID_SQL, id);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Delete book with id = %s failed", id));
        }

        log.info("Delete book successfully with id{}", id);
    }

    @Override
    public List<BookDto> getBookByUserId(Long userId) {
        exceptionUtility.throwServiceExceptionIfNull(userId, "Get book by userId failed : userId is null");

        List<BookDto> bookDtoList = null;
        try {
            final String GET_BOOK_BY_USER_ID_SQL = "SELECT * FROM BOOK WHERE USER_ID=?";
            bookDtoList = jdbcTemplate.query(
                    GET_BOOK_BY_USER_ID_SQL,
                    (rs, rowNum) -> bookMapper.bookToBookDto(bookMapper.resultSetToBook(rs)),
                    userId);

        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Get book by userId = %s failed", userId));
        }

        log.info("Get book by userId = {} successfully", userId);
        return bookDtoList;
    }
}
