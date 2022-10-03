package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.utility.ExceptionUtility;
import com.edu.ulab.app.utility.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ExceptionUtility exceptionUtility;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, ExceptionUtility exceptionUtility) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.exceptionUtility = exceptionUtility;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        exceptionUtility.throwServiceExceptionIfNull(userDto, "Create user failed : userDto is null");

        userDto.setId(IdGenerator.nextUserId());
        Person person = userMapper.userDtoToPerson(userDto);
        try {
            person = userRepository.save(person);
        } catch (Exception e) {
            exceptionUtility.throwServiceException("Create user failed.");
        }

        log.info("Create user successfully {}", person);
        return userMapper.personToUserDto(person);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        exceptionUtility.throwServiceExceptionIfNull(userDto, "Update user failed : userDto is null");
        exceptionUtility.throwServiceExceptionIfNull(userDto.getId(), "Update user failed : userDto.getId() returns null");

        if(!userRepository.existsById(userDto.getId())) {
            exceptionUtility.throwNotFoundException(String.format("Update user failed : user with id = %s not exist", userDto.getId()));
        }

        Person userEntity = userMapper.userDtoToPerson(userDto);
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Update user with id = %s failed", userDto.getId()));
        }

        log.info("Update user successfully {}", userEntity);
        return userMapper.personToUserDto(userEntity);
    }

    @Override
    public UserDto getUserById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Get user failed : id is null");

        Person userEntity = null;
        try {
            userEntity = userRepository.findById(id).get();
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Get user with id = %s failed", id));
        }

        exceptionUtility.throwNotFoundExceptionIfNull(userEntity, String.format("Get user failed : user with id = %s not exist", id));
        log.info("Get user successfully {}", userEntity);
        return userMapper.personToUserDto(userEntity);
    }

    @Override
    public void deleteUserById(Long id) {
        exceptionUtility.throwServiceExceptionIfNull(id, "Delete user failed : id is null");

        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            exceptionUtility.throwServiceException(String.format("Delete user with id = %s failed", id));
        }

        log.info("Delete user with id = {} successfully", id);
    }
}
