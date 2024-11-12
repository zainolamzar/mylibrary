package com.example.MyLibrary.repositories;

import com.example.MyLibrary.models.Borrowed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowedRepository extends JpaRepository<Borrowed, Integer> {
    List<Borrowed> findByIsReturn(Borrowed.IsReturn isReturn);

    Optional<Borrowed> findById(int id);
}