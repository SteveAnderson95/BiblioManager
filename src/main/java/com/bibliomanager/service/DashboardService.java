package com.bibliomanager.service;

import com.bibliomanager.model.Loan;
import com.bibliomanager.repository.BookRepository;
import com.bibliomanager.repository.CategoryRepository;
import com.bibliomanager.repository.LoanRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DashboardService {

    private final BookRepository bookRepo = new BookRepository();
    private final CategoryRepository categoryRepo = new CategoryRepository();
    private final LoanRepository loanRepo = new LoanRepository();

    //for the 4 statistics cards
    public Map<String, Object> getQuickStats() {
        int totalBooks = bookRepo.getTotalBooksCount();
        int availableBooks = bookRepo.getAvailableBooksCount();
        double availabilityRate = (totalBooks > 0) ? (availableBooks * 100.0 / totalBooks) : 0;

        return Map.of(
                "totalBooks", totalBooks,
                "totalCategories", categoryRepo.getTotalCategoriesCount(),
                "availableBooks", availableBooks,
                "availabilityRate", (int) availabilityRate,
                "activeLoans", loanRepo.countOngoingLoans(),
                "overdueLoans", loanRepo.countOverdueLoans()
        );
    }

    //for the BarChart
    public Map<String, Integer> getWeeklyTrend() {
        return loanRepo.getLoansPerDayLastWeek();
    }

    //for the PieChart
    public Map<String, Integer> getCategoryData() {
        return bookRepo.getBooksCountByCategory();
    }

    //for the tableviews
    public List<Loan> getRecentBorrowings() {
        return loanRepo.findRecentLoans(5); //I just take 5 for the dashboard
    }

    public List<Loan> getOverdueAlerts() {
        return loanRepo.findOverdueLoans();
    }
}
