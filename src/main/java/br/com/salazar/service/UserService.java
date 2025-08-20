package br.com.salazar.service;

import br.com.salazar.model.dto.UsersResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://dummyjson.com/users";

    public UsersResponseDto getUsers() {
        return restTemplate.getForObject(API_URL, UsersResponseDto.class);
    }
}