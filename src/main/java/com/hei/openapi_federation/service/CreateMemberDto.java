package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.Gender;

import java.time.LocalDate;
import java.util.List;

public class CreateMemberDto {
    public boolean isRegistrationFeePaid() {
            return false;
    }

    public boolean isMembershipDuesPaid() {
        return false;
    }

    public String getCollectivityIdentifier() {
            return null;
    }

    public List<String> getReferees() {
            return null;
    }

    public String getFirstName() {
        return null;
    }

    public String getLastName() {
        return null;
    }

    public LocalDate getBirthDate() {
        return null;
    }

    public Gender getGender() {
        return null;
    }

    public String getAddress() {
        return null;
    }

    public String getProfession() {
        return null;
    }

    public int getPhoneNumber() {
        return 0;
    }

    public String getEmail() {
            return null;
    }
}
