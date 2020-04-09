package com.example.springbootaplication.service;

import com.example.springbootaplication.Exception.UsernameOrIdNotFound;
import com.example.springbootaplication.dto.ChangePasswordForm;
import com.example.springbootaplication.entity.User;
import com.example.springbootaplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository repository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Iterable<User> getAllUsers() {
        return repository.findAll();
    }

    private boolean checkUsernameAvailable(User user) throws Exception {
        Optional<User> userFound = repository.findByUsername(user.getUsername());
        if (userFound.isPresent()) {
            throw new Exception("Username no disponible");
        }
        return true;
    }

    private boolean checkPasswordValid(User user) throws Exception {
        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
            throw new Exception("Confirm Password es obligatorio");
        }

        if ( !user.getPassword().equals(user.getConfirmPassword())) {
            throw new Exception("Password y Confirm Password no son iguales");
        }
        return true;
    }

    @Override
    public User getUserById(Long id) throws UsernameOrIdNotFound {
        return repository.findById(id).orElseThrow(() -> new UsernameOrIdNotFound("El ID del usuario para editar no existe."));
    }

    @Override
    public User updateUser(User fromUser) throws Exception {
        User toUser = getUserById(fromUser.getId());
        mapUser(fromUser, toUser);
        return repository.save(toUser);
    }

    protected void mapUser(User from,User to) {
        to.setUsername(from.getUsername());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setRoles(from.getRoles());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUser(Long id) throws UsernameOrIdNotFound {
        User user = getUserById(id);
        repository.delete(user);
    }

    @Override
    public User changePassword(ChangePasswordForm form) throws Exception {
        User user = getUserById(form.getId());

        if(!isLoggedUserADMIN() &&  !user.getPassword().equals(form.getCurrentPassword())) {
            throw new Exception("Current Password Incorrect.");
        }

        if ( form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new Exception("New Password must be different than Current Password!");
        }

        if( !form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new Exception ("Nuevo Password y Confirm Password no coinciden.");
        }

        String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
        user.setPassword(encodePassword);
        return repository.save(user);
    }

    private boolean isLoggedUserADMIN() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggedUser = null;
        if (principal instanceof UserDetails) {
            loggedUser = (UserDetails) principal;

            loggedUser.getAuthorities().stream()
                    .filter(x -> "ADMIN".equals(x.getAuthority() ))
                    .findFirst().orElse(null); //loggedUser = null;
        }
        return loggedUser != null ?true :false;
    }


    @Override
    public User createUser(User user) throws Exception {
        if (checkUsernameAvailable(user) && checkPasswordValid(user)) {
            user = repository.save(user);
        }
        return user;
    }
}
