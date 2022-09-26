package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.UserBook;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserBookRepository extends CrudRepository<UserBook, Long> {

    List<UserBook> findByUserId(long userId);

    Long removeByUserId(long userId);
}
