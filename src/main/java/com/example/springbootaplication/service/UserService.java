package com.example.springbootaplication.service;

import com.example.springbootaplication.Exception.UsernameOrIdNotFound;
import com.example.springbootaplication.dto.ChangePasswordForm;
import com.example.springbootaplication.entity.User;

public interface UserService {
    public Iterable<User> getAllUsers();

    public User createUser(User user) throws Exception;

    public User getUserById(Long id) throws Exception;

    public User updateUser(User user) throws Exception;

    public void deleteUser(Long id) throws UsernameOrIdNotFound;

    public User changePassword(ChangePasswordForm form) throws Exception;
}
