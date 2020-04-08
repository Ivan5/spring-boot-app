package com.example.springbootaplication.repository;

import com.example.springbootaplication.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    public Set<User> findByEmail(String email);
}
