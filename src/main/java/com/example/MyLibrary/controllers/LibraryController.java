package com.example.MyLibrary.controllers;

import com.example.MyLibrary.models.Book;
import com.example.MyLibrary.models.Borrowed;
import com.example.MyLibrary.models.User;
import com.example.MyLibrary.repositories.BookRepository;
import com.example.MyLibrary.repositories.BorrowedRepository;
import com.example.MyLibrary.services.LibraryService;
import com.example.MyLibrary.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Locale;
import java.util.Optional;

@Controller
public class LibraryController {

    @Autowired
    private LibraryService libraryService;
    @Autowired
    private UserService userService;
    @Autowired
    private BorrowedRepository borrowedRepository;
    @Autowired
    private BookRepository bookRepository;

    // Mapping for Home page
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("allBorrowed", libraryService.getAllBorrowedBooks());
        return "library/index";
    }

    @GetMapping("/books")
    public String bookOverview(Model model) {
        model.addAttribute("allBooks", libraryService.getAllBooks());
        return "library/book-overview";
    }

    @GetMapping("/create-book")
    public String createBookPage() {
        return "library/book-create";
    }

    @PostMapping("/create-book")
    public String createBook(@RequestParam String name, @RequestParam String category, @RequestParam String isAvailable) {
        Book newBook = new Book();
        newBook.setName(name);
        newBook.setCategory(category);
        newBook.setIsAvailable(Book.Availability.valueOf(isAvailable));

        libraryService.saveBook(newBook);
        return "redirect:/books";
    }

    // View Book details
    @GetMapping("/view-book/{id}")
    public String viewBook(@PathVariable("id") int id, Model model) {
        Book book = libraryService.getBookById(id);
        model.addAttribute("book", book);
        return "library/book-view";
    }

    // Edit Book
    @GetMapping("/edit-book/{id}")
    public String editBook(@PathVariable("id") int id, Model model) {
        Book book = libraryService.getBookById(id);
        model.addAttribute("book", book);
        return "library/book-edit";
    }

    // Save edited Book
    @PostMapping("/edit-book/{id}")
    public String updateBook(@PathVariable("id") int id, @ModelAttribute Book book) {
        libraryService.updateBook(id, book);
        return "redirect:/books";
    }

    // Delete Book
    @GetMapping("/delete-book/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        libraryService.deleteBook(id);
        return "redirect:/books";
    }

    // Mapping for librarians page
    @GetMapping("/librarians")
    public String userOverview(Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        return "library/user-overview";
    }

    @GetMapping("/librarians/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "library/user-create";
    }

    @PostMapping("/librarians/save")
    public String saveUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/librarians";
    }

    @GetMapping("/view-user/{id}")
    public String viewUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "library/user-view";
    }

    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "library/user-edit";
    }

    @PostMapping("/update-user/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute User user) {
        user.setId(id);
        userService.updateUser(user);
        return "redirect:/librarians";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return "redirect:/librarians";
    }

    @GetMapping("/borrow-form")
    public String showBorrowForm(Model model) {
        model.addAttribute("availableBooks", libraryService.getAvailableBooks());
        model.addAttribute("librarians", libraryService.getAllLibrarians());
        return "library/borrow-form";
    }

    @PostMapping("/borrow")
    public String borrowBook(
            @RequestParam int bookId,
            @RequestParam int assignBy,
            @RequestParam String borrower,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            // Parse the dates with the correct format (yyyy-MM-dd)
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            // Parse as java.util.Date first
            java.util.Date parsedStartDate = formatter.parse(startDate);
            java.util.Date parsedEndDate = formatter.parse(endDate);

            // Convert java.util.Date to java.sql.Date
            Date sqlStartDate = new Date(parsedStartDate.getTime());
            Date sqlEndDate = new Date(parsedEndDate.getTime());

            // Borrow the book by calling the service
            libraryService.borrowBook(bookId, assignBy, borrower, sqlStartDate, sqlEndDate);

            // Update the book's availability status to 'Unavailable'
            libraryService.updateBookAvailability(bookId, "Unavailable");

        } catch (ParseException e) {
            e.printStackTrace();
            return "redirect:/borrow-form?error=dateParse";
        }
        return "redirect:/";
    }

    @GetMapping("/borrow/edit/{id}")
    public String editBorrowedTransaction(@PathVariable("id") int borrowedId, Model model) {
        // Fetch the borrowed transaction by its ID
        Optional<Borrowed> borrowedOptional = borrowedRepository.findById(borrowedId);

        if (borrowedOptional.isPresent()) {
            Borrowed borrowed = borrowedOptional.get();
            model.addAttribute("borrowed", borrowed);
            return "library/borrow-edit";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/borrow/save")
    public String saveEditedBorrowedTransaction(@RequestParam int id,
                                                @RequestParam String borrower,
                                                @RequestParam String startDate,
                                                @RequestParam String endDate
    ) {
        // Find the borrowed transaction by ID
        Optional<Borrowed> borrowedOptional = borrowedRepository.findById(id);
        if (borrowedOptional.isPresent()) {
            Borrowed borrowed = borrowedOptional.get();

            // Update the borrowed details
            borrowed.setBorrower(borrower);

            // Convert String to java.sql.Date using Date.valueOf()
            borrowed.setStartDate(Date.valueOf(startDate));  // Convert to SQL Date
            borrowed.setEndDate(Date.valueOf(endDate));      // Convert to SQL Date

            // Save the updated borrowed transaction
            borrowedRepository.save(borrowed);
        }

        return "redirect:/"; // Redirect to the home page after saving
    }



    @PostMapping("/borrow/return")
    public String returnBook(@RequestParam int borrowedId) {
        // Get the transaction from the database
        Optional<Borrowed> borrowedOptional = borrowedRepository.findById(borrowedId);

        if (borrowedOptional.isPresent()) {
            Borrowed borrowed = borrowedOptional.get();

            // Update the borrowed table (set isReturn to 'Yes')
            borrowed.setIsReturn(Borrowed.IsReturn.valueOf("Yes"));
            borrowedRepository.save(borrowed);

            // Get the book associated with the transaction
            Book book = borrowed.getBook();

            // Set the book's availability back to 'Available'
            book.setIsAvailable(Book.Availability.Available);
            bookRepository.save(book);
        }

        return "redirect:/";
    }


}