package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nationalId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByNationalId(nationalId);
        return new org.springframework.security.core.userdetails.User(user.get().getNationalId(), user.get().getPassword() , new ArrayList<>());
    }
}
