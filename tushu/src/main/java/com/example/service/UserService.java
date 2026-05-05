package com.example.service;

import com.example.dto.UserDTO;
import com.example.entity.User;

import java.util.List;

public interface UserService {
    List<UserDTO> findAll();
    UserDTO findById(Long id);
    UserDTO save(UserDTO userDTO);
    void deleteById(Long id);
    UserDTO findByUsername(String username);
}
