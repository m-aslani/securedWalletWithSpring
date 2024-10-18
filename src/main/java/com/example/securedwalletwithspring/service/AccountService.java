package com.example.securedwalletwithspring.service;

import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.repository.AccountRepository;
import com.example.securedwalletwithspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    // show user information
    public Optional<User> findUserByNationalId(EditedUserDto editedUserDto){
        Optional<User> user = userRepository.findByNationalId(editedUserDto.getNationalId());
        if(user.isEmpty()){
            throw new UserNotFoundException("User with National ID " + editedUserDto.getNationalId() + " not found");
        }
        return user;
    }

    //update phone number
    public String updateUserPhoneNumber(EditedUserDto editedUserDto){
        Optional<User> user = findUserByNationalId(editedUserDto);
        user.get().setPhoneNumber(editedUserDto.getPhoneNumber());
        userRepository.save(user.get());
        return editedUserDto.getPhoneNumber();
    }

    //update email
    public String updateUserEmail(EditedUserDto editedUserDto){
        Optional<User> user = findUserByNationalId(editedUserDto);
        user.get().setEmail(editedUserDto.getEmail());
        userRepository.save(user.get());
        return editedUserDto.getEmail();
    }

    //update password
    public String updateUserPassword(EditedUserDto editedUserDto){
        Optional<User> user = findUserByNationalId(editedUserDto);
        user.get().setPassword(editedUserDto.getPassword());
        userRepository.save(user.get());
        return editedUserDto.getPassword();
    }

    //delete user account
    public void deleteUser(EditedUserDto editedUserDto){
        Optional<User> user = findUserByNationalId(editedUserDto);
        userRepository.delete(user.get());
    }


}
