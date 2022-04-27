package com.example.forum.converter;

import com.example.forum.model.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class RoleConverter  {

/*    @Override
    public String convertToDatabaseColumn(Role role) {
        return Optional
                .ofNullable(role)
                .map(Role::toString)
                .orElse(null);
    }

    @Override
    public Role convertToEntityAttribute(String s) {
        return Objects.isNull(s)
                ? null
                : Arrays.stream(Role.values())
                .filter(enumValue -> enumValue.toString().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }*/
}
