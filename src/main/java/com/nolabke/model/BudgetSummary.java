package com.nolabke.model;

import java.math.BigDecimal;

public class BudgetSummary {

    private final BigDecimal income;
    private final BigDecimal expenses;
    private final BigDecimal balance;


    public BudgetSummary(
            BigDecimal income,
            BigDecimal expenses,
            BigDecimal balance
    ) {
        this.income = income;
        this.expenses = expenses;
        this.balance = balance;
    }


    public BigDecimal getIncome() {
        return income;
    }


    public BigDecimal getExpenses() {
        return expenses;
    }


    public BigDecimal getBalance() {
        return balance;
    }
}