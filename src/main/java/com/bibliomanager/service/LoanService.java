package com.bibliomanager.service;

import com.bibliomanager.model.Loan;
import com.bibliomanager.repository.LoanRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class LoanService {

    private final LoanRepository repo = new LoanRepository();

    public int getActiveLoansCount() {
        return repo.countOngoingLoans();
    }

    public int getOverdueLoansCount() {
        return repo.countOverdueLoans();
    }

    public Map<String, Integer> getWeeklyTrend() {
        return repo.getLoansPerDayLastWeek();
    }

    public List<Loan> getRecentBorrowings() {
        return repo.findRecentLoans(10);
    }

    public List<Loan> getOverdueAlerts() {
        return repo.findOverdueLoans();
    }

    public List<Loan> getActiveLoansByStudent(long studentId) {
        try {
            return repo.findActiveLoansByStudent(studentId);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching student loans", e);
        }
    }
}