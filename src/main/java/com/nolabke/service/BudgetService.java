package com.nolabke.service;

import com.nolabke.model.BudgetEntry;
import com.nolabke.model.BudgetSummary;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class BudgetService {

    private final BudgetStorage storage;

    public BudgetService(BudgetStorage storage) {
        this.storage = storage;
    }

    public List<BudgetEntry> getEntries(YearMonth month) {
        return storage.load(month);
    }

    public void add(BudgetEntry entry) {

        YearMonth month =
                YearMonth.from(entry.getDate());

        List<BudgetEntry> entries =
                storage.load(month);

        entries.add(entry);

        storage.save(month, entries);
    }

    public void update(BudgetEntry updatedEntry) {

        YearMonth month =
                YearMonth.from(updatedEntry.getDate());

        List<BudgetEntry> entries =
                storage.load(month);

        entries.replaceAll(e ->
                e.getId().equals(updatedEntry.getId())
                        ? updatedEntry
                        : e
        );

        storage.save(month, entries);
    }


    public void delete(BudgetEntry entry) {

        YearMonth month =
                YearMonth.from(entry.getDate());

        List<BudgetEntry> entries =
                storage.load(month);

        entries.removeIf(e ->
                e.getId().equals(entry.getId())
        );

        storage.save(month, entries);
    }

    public BudgetSummary calculateBudgetSummary(
            List<BudgetEntry> entries
    ) {

        BigDecimal income = BigDecimal.valueOf(0);
        BigDecimal expenses = BigDecimal.valueOf(0);

        for (BudgetEntry entry : entries) {
            if (entry.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
                income = income.add(entry.getAmount());
            } else {
                expenses = expenses.add(entry.getAmount().abs());
            }
        }

        BigDecimal balance = income.subtract(expenses);

        return new BudgetSummary(
                income,
                expenses,
                balance
        );
    }
}