package com.example.MyLibrary.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "borrowed")
public class Borrowed {

    public enum IsReturn {
        Yes, No
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "assign_by", nullable = false)
    private User librarian;

    private String borrower;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private IsReturn isReturn;

    @Transient
    private String status;

    public void calculateStatus() {
        Date today = new Date();
        if (today.before(endDate)) {
            this.status = "On Time";
        } else {
            this.status = "Overdue";
        }
    }

    public long calculateDaysLeft() {
        long diffInMillies = Math.max(endDate.getTime() - new Date().getTime(), 0);
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getLibrarian() {
        return librarian;
    }

    public void setLibrarian(User librarian) {
        this.librarian = librarian;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public IsReturn getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(IsReturn isReturn) {
        this.isReturn = isReturn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}