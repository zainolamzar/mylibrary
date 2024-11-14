package com.example.MyLibrary.services;

import com.example.MyLibrary.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public boolean authenticate(String username, String password) {
        return adminRepository.findByUsernameAndPassword(username, password).isPresent();
    }
}