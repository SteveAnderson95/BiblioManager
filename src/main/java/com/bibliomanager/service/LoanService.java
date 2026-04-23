package com.bibliomanager.service;

import com.bibliomanager.model.Loan;
import com.bibliomanager.repository.BookRepository;
import com.bibliomanager.repository.LoanRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LoanService {

    private final LoanRepository loanRepo = new LoanRepository();
    private final BookRepository bookRepo = new BookRepository();


    public List<Loan> getAllLoans(String status, String period) {
        try {
            return loanRepo.findAllLoans(status, period);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all loans", e);
        }
    }

    public List<Loan> searchAllLoans(String query, String status, String period) {
        try {
            return loanRepo.searchAllLoans(query, status, period);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching loans", e);
        }
    }

    public List<Loan> getReturnedLoans(String period, String type) {
        try {
            return loanRepo.findReturnedLoans(period, type);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching returned loans", e);
        }
    }

    public List<Loan> searchReturnedLoans(String query, String period, String type) {
        try {
            return loanRepo.searchReturnedLoans(query, period, type);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching returned loans", e);
        }
    }

    public double getPunctualityRate() {
        try {
            int total = loanRepo.countTotalReturned();
            int onTime = loanRepo.countReturnedOnTime();
            if (total == 0) return 0.0;
            return (onTime * 100.0) / total;
        } catch (SQLException e) {
            throw new RuntimeException("Error computing punctuality rate", e);
        }
    }

    public int getExpectedReturnsToday() {
        try {
            return loanRepo.countExpectedReturnsToday();
        } catch (SQLException e) {
            throw new RuntimeException("Error counting expected returns", e);
        }
    }

    public double getAverageOverdueDays() {
        try {
            return loanRepo.averageOverdueDays();
        } catch (SQLException e) {
            throw new RuntimeException("Error computing average overdue", e);
        }
    }

    public Map<String, Integer> getReturnsThisWeek() {
        try {
            return loanRepo.getReturnsPerDayThisWeek();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching weekly returns", e);
        }
    }

    public List<Loan> getActiveLoans() {
        try {
            return loanRepo.findActiveLoans();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active loans", e);
        }
    }

    public List<Loan> searchActiveLoans(String query, String status) {
        try {
            return loanRepo.searchActiveLoans(query, status);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching loans", e);
        }
    }

    public void registerLoan(Loan loan) {
        validateLoan(loan);
        try {
            loanRepo.insert(loan);
            // We decrement th book availability
            bookRepo.decrementAvailability(loan.getBook().getId());
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY"))
                throw new RuntimeException("Invalid student or book.");
            throw new RuntimeException("Error registering loan", e);
        }
    }

    public void markAsReturned(long loanId, long bookId) {
        try {
            loanRepo.markAsReturned(loanId, LocalDate.now());
            // We increment again th book availability
            bookRepo.incrementAvailability(bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Error marking loan as returned", e);
        }
    }

    public int getReturnedTodayCount() {
        try {
            return loanRepo.countReturnedToday();
        } catch (SQLException e) {
            throw new RuntimeException("Error counting returns", e);
        }
    }

    public List<Loan> getMostOverdue() {
        try {
            return loanRepo.findMostOverdue();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching most overdue", e);
        }
    }

    private void validateLoan(Loan loan) {
        if (loan.getStudent() == null)
            throw new IllegalArgumentException("Student is required.");
        if (loan.getBook() == null)
            throw new IllegalArgumentException("Book is required.");
        if (loan.getDueDate() == null)
            throw new IllegalArgumentException("Due date is required.");
        if (loan.getDueDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Due date must be in the future.");
        if (!loan.getBook().isAvailable())
            throw new IllegalArgumentException("This book is not available.");
    }

    public int getActiveLoansCount() {
        return loanRepo.countOngoingLoans();
    }

    public int getOverdueLoansCount() {
        return loanRepo.countOverdueLoans();
    }

    public Map<String, Integer> getWeeklyTrend() {
        return loanRepo.getLoansPerDayLastWeek();
    }

    public List<Loan> getRecentBorrowings() {
        return loanRepo.findRecentLoans(10);
    }

    public List<Loan> getOverdueAlerts() {
        return loanRepo.findOverdueLoans();
    }

    public List<Loan> getActiveLoansByStudent(long studentId) {
        try {
            return loanRepo.findActiveLoansByStudent(studentId);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching student loans", e);
        }
    }
}