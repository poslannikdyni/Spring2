package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.UserBook;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserBookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.utility.ExceptionUtility;
import com.edu.ulab.app.utility.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final UserBookRepository userBookRepository;

    private final BookMapper bookMapper;

    private final ExceptionUtility exceptionUtility;

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository,
                           UserBookRepository userBookRepository,
                           BookMapper bookMapper,
                           ExceptionUtility exceptionUtility) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.userBookRepository = userBookRepository;
        this.bookMapper = bookMapper;
        this.exceptionUtility = exceptionUtility;

    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        exceptionUtility.throwServiceExceptionIfNull(bookDto, "Create book failed : bookDto is null");
        exceptionUtility.throwServiceExceptionIfNull(bookDto.getUserId(), "Create book failed : bind userId is null");

        bookDto.setId(IdGenerator.nextBookId());

        // Check user exist.
        if (!userRepository.existsById(bookDto.getUserId())) {
            exceptionUtility.throwNotFoundException(String.format("Create book failed : bind user with id = %s not exist", bookDto.getUserId()));
        }

        // Create book.
        Book book = bookMapper.bookDtoToBook(bookDto);
        try {
            book = bookRepository.save(book);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(e, "Create book failed.");
        }
        log.info("Create book successfully {}", book);

        // Create user-book binding.
        UserBook userBook = null;
        try {
            userBook = userBookRepository.save(new UserBook(IdGenerator.nextUserBookId(), book.getUserId(), book.getId()));
        } catch (Exception e) {
            exceptionUtility.throwServiceException("Create user-book binding failed.");
        }

        log.info("Create user-book binding successfully {}", userBook);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        exceptionUtility.throwServiceExceptionIfNull(bookDto, "Update book failed : bookDto is null");
        exceptionUtility.throwServiceExceptionIfNull(bookDto.getId(), "Update book failed : bookDto.getId() returns null");

        if(!bookRepository.existsById(bookDto.getId())) {
            exceptionUtility.throwNotFoundException(String.format("Update book failed : book with id = %s not exist", bookDto.getId()));
        }

        Book book = bookMapper.bookDtoToBook(bookDto);
        try {
            book = bookRepository.save(book);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Update book with id = %s failed", book.getId()));
        }

        log.info("Update book successfully {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public BookDto getBookById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Get book failed : id is null");

        Book book = null;
        try {
            book = bookRepository.findById(id).get();
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
            bookRepository.deleteById(id);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Delete book with id = %s failed", id));
        }

        log.info("Delete book successfully with id{}", id);
    }

    @Override
    public List<BookDto> getBookByUserId(Long userId) {
        exceptionUtility.throwServiceExceptionIfNull(userId, "Get book by userId failed : userId is null");

        List<BookDto> bookDtoList = Collections.emptyList();
        try {
            bookDtoList = userBookRepository.findByUserId(userId)
                    .stream()
                    .map(userBook -> bookRepository.findById(userBook.getBookId()).get())
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookToBookDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Get book by userId = %s failed", userId));
        }

        log.info("Get book by userId = {} successfully", userId);
        return bookDtoList;
    }

    @Override
    public void deleteUserBookBinding(Long userId) {
        exceptionUtility.throwServiceExceptionIfNull(userId, "Delete user book by userId failed : userId is null");

        try {
            userBookRepository.removeByUserId(userId);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Delete user book by userId = %s failed", userId));
        }

        log.info("Delete user book by userId successfully. UserId = {}", userId);
    }
}
