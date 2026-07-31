package com.nolabke.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BudgetEntry {


    private UUID id;

    private LocalDate date;

    private String description;

    private BigDecimal amount;


    public BudgetEntry() {}


    public BudgetEntry(
            LocalDate date,
            String description,
            BigDecimal amount
    ) {

        this.id = UUID.randomUUID();
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    public BudgetEntry(
            UUID id,
            LocalDate date,
            String description,
            BigDecimal amount
    ) {

        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }


    public UUID getId() {
        return id;
    }


    public LocalDate getDate() {
        return date;
    }


    public String getDescription() {
        return description;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}