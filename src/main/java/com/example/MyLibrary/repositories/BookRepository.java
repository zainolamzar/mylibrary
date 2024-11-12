// src/main/java/com/example/MyLibrary/repositories/BookRepository.java
package com.example.MyLibrary.repositories;

import com.example.MyLibrary.models.Book;
import com.example.MyLibrary.models.Book.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByIsAvailable(Availability isAvailable);

    Book save(Book book);
}