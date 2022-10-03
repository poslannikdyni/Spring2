package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.web.request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface UserMapper {

    default Person resultSetToUser(ResultSet rs) throws SQLException {
        return new Person(
                rs.getLong("ID"),
                rs.getString("FULL_NAME"),
                rs.getString("TITLE"),
                rs.getInt("AGE")
        );
    }
    UserDto userRequestToUserDto(UserRequest userRequest);

    UserRequest userDtoToUserRequest(UserDto userDto);

    Person userDtoToPerson(UserDto userDto);

    UserDto personToUserDto(Person person);

    void updatePersonFromDto(UserDto dto, @MappingTarget Person entity);
}