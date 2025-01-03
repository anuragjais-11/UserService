package com.example.userservice.dtos;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponseDto {
    private String name;
    private String email;
    private List<Role> roleList;

    public static UserResponseDto from(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setEmail(user.getEmail());
        responseDto.setName(user.getName());
        responseDto.setRoleList(user.getRoles());

        return responseDto;
    }
}
