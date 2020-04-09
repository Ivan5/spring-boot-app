package com.example.springbootaplication.service;

import com.example.springbootaplication.entity.Role;
import com.example.springbootaplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.springbootaplication.entity.User appUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User does not exist!"));

        Set grantList = new HashSet();

        //Crear la lista de los roles/accessos que tienen el usuarios
        for (Role role: appUser.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getDescription());
            grantList.add(grantedAuthority);
        }
        //Crear y retornar Objeto de usuario soportado por Spring Security
        UserDetails user = (UserDetails) new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), grantList);
        return user;
    }
}
