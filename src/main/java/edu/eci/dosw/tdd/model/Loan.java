package edu.eci.dosw.tdd.model;

import lombok.Data;

import java.util.Date;
@Data
public class Loan {
    private String status;
    private Book book;
    private User user;
    private Date loanDate;
    private Date returnDate;

}