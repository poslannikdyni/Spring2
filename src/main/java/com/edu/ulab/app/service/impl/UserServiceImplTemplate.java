package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.utility.ExceptionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final ExceptionUtility exceptionUtility;


    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper, ExceptionUtility exceptionUtility) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
        this.exceptionUtility = exceptionUtility;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        exceptionUtility.throwServiceExceptionIfNull(userDto, "Update user failed : userDto is null");
        exceptionUtility.throwServiceExceptionIfNull(userDto.getId(), "Update user failed : userDto.getId() returns null");

        if (getUserById(userDto.getId()) == null) {
            exceptionUtility.throwNotFoundException(String.format("Update user failed : user with id = %s not exist", userDto.getId()));
        }

        final String UPDATE_SQL = "UPDATE PERSON SET AGE=?, FULL_NAME=?, TITLE=? WHERE ID = ?";
        Person user = userMapper.userDtoToPerson(userDto);
        int countRowHasChanged = 0;
        try {
            countRowHasChanged = jdbcTemplate.update(UPDATE_SQL,
                    user.getAge(),
                    user.getFullName(),
                    user.getTitle(),
                    user.getId()
            );
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Update user with id = %s failed", userDto.getId()));
        }

        if (countRowHasChanged == 0) {
            exceptionUtility.throwServiceException(String.format("Update user failed. Database has zero row with id = %s", user.getId()));
        } else if (countRowHasChanged > 1) {
            exceptionUtility.throwServiceException(String.format("Update user potentially unsafe. Database has %s row with id = %s", countRowHasChanged, user.getId()));
        }

        log.info("Update user successfully {}", user);
        return userMapper.personToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Get user failed : id is null");

        Person user = null;
        try {
            final String GET_USER_BY_ID_SQL = "SELECT * FROM PERSON WHERE ID=? LIMIT 1";
            user = jdbcTemplate.queryForObject(
                    GET_USER_BY_ID_SQL,
                    (rs, rowNum) -> userMapper.resultSetToUser(rs),
                    id
            );

        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Get user with id = %s failed", id));
        }

        exceptionUtility.throwNotFoundExceptionIfNull(user, String.format("Get user failed : user with id = %s not exist", id));
        log.info("Get user successfully {}", user);
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Delete user failed : id is null");

        try {
            final String DELETE_USER_BY_ID_SQL = "DELETE FROM PERSON WHERE ID = ?";
            jdbcTemplate.update(DELETE_USER_BY_ID_SQL, id);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Delete user with id = %s failed", id));
        }

        log.info("Delete user with id = {} successfully", id);
    }
}
