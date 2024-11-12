package com.example.MyLibrary.repositories;

import com.example.MyLibrary.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Additional queries if needed
}