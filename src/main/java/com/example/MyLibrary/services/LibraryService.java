package com.example.MyLibrary.services;

import com.example.MyLibrary.models.Book;
import com.example.MyLibrary.models.Borrowed;
import com.example.MyLibrary.models.User;
import com.example.MyLibrary.repositories.BookRepository;
import com.example.MyLibrary.repositories.BorrowedRepository;
import com.example.MyLibrary.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowedRepository borrowedRepository;
    @Autowired
    private UserRepository userRepository;

    public void saveBook(Book book) {
        bookRepository.save(book);  // Save the book to the database
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void updateBook(int id, Book book) {
        if (bookRepository.existsById(id)) {
            book.setId(id);
            bookRepository.save(book);
        }
    }

    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public List<User> getAllLibrarians() {
        return userRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByIsAvailable(Book.Availability.Available);
    }

    public List<Borrowed> getAllBorrowedBooks() {
        List<Borrowed> borrowedBooks = borrowedRepository.findByIsReturn(Borrowed.IsReturn.No);
        borrowedBooks.forEach(Borrowed::calculateStatus); // Calculate status for each book
        return borrowedBooks;
    }

    public void borrowBook(int bookId, int assignBy, String borrower, Date startDate, Date endDate) {
        Borrowed borrowed = new Borrowed();
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User librarian = userRepository.findById(assignBy).orElseThrow(() -> new RuntimeException("Librarian not found"));

        borrowed.setBook(book);
        borrowed.setLibrarian(librarian);
        borrowed.setBorrower(borrower);
        borrowed.setStartDate(startDate);
        borrowed.setEndDate(endDate);
        borrowed.setIsReturn(Borrowed.IsReturn.No); // Set to No when borrowing

        borrowedRepository.save(borrowed);
    }

    public void updateBookAvailability(int bookId, String availabilityStatus) {
        // Fetch the book by its ID
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            // Set the availability status to 'Unavailable'
            book.setIsAvailable(availabilityStatus.equals("Unavailable") ? Book.Availability.Unavailable : Book.Availability.Available);

            // Save the updated book back to the database
            bookRepository.save(book);
        } else {
            // Handle case where book was not found
            throw new RuntimeException("Book not found with ID: " + bookId);
        }
    }
}