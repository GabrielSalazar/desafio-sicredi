package br.com.salazar.model.dto;

import java.util.List;

public class UsersResponseDto {
    private List<UserDto> users;

    public List<UserDto> getUsers() { return users; }
    public void setUsers(List<UserDto> users) { this.users = users; }
}