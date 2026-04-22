package com.hei.openapi_federation.entity;


public class BankAccount extends FinancialAccount {

    private String holderName;
    private Bank bankName;
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountKey;
    private Double amount;

    public BankAccount() {}

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public Bank getBankName() { return bankName; }
    public void setBankName(Bank bankName) { this.bankName = bankName; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public String getBankBranchCode() { return bankBranchCode; }
    public void setBankBranchCode(String bankBranchCode) { this.bankBranchCode = bankBranchCode; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankAccountKey() { return bankAccountKey; }
    public void setBankAccountKey(String bankAccountKey) { this.bankAccountKey = bankAccountKey; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}