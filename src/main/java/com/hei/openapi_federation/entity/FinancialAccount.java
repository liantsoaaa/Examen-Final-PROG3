package com.hei.openapi_federation.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CashAccount.class,          name = "CASH"),
        @JsonSubTypes.Type(value = MobileBankingAccount.class, name = "MOBILE_BANKING"),
        @JsonSubTypes.Type(value = BankAccount.class,          name = "BANK_ACCOUNT")
})
public abstract class FinancialAccount {
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}