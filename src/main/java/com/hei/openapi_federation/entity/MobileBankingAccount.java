package com.hei.openapi_federation.entity;


public class MobileBankingAccount extends FinancialAccount {

    private String holderName;
    private MobileBankingService mobileBankingService;
    private String mobileNumber;
    private Double amount;

    public MobileBankingAccount() {}

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public MobileBankingService getMobileBankingService() { return mobileBankingService; }
    public void setMobileBankingService(MobileBankingService mobileBankingService) {
        this.mobileBankingService = mobileBankingService;
    }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}