package com.hei.openapi_federation.entity;


public class CashAccount extends FinancialAccount {

    private Double amount;

    public CashAccount() {}

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}