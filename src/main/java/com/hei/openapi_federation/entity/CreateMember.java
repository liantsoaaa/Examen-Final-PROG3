package com.hei.openapi_federation.entity;

import java.time.LocalDate;
import java.util.List;


public class CreateMember {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private String collectivityIdentifier;
    private List<String> referees;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;

    public CreateMember() {}

    public CreateMember(String firstName, String lastName, LocalDate birthDate, Gender gender, String address, String profession, String phoneNumber, String email, MemberOccupation occupation, String collectivityIdentifier, List<String> referees, boolean registrationFeePaid, boolean membershipDuesPaid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.profession = profession;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.occupation = occupation;
        this.collectivityIdentifier = collectivityIdentifier;
        this.referees = referees;
        this.registrationFeePaid = registrationFeePaid;
        this.membershipDuesPaid = membershipDuesPaid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Gender getGender() {
        return gender;  
    }

    public String getAddress() {
        return address;
    }

    public String getProfession() {
        return profession;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public MemberOccupation getOccupation() {
        return occupation;
    }

    public String getCollectivityIdentifier() {
        return collectivityIdentifier;
    }

    public List<String> getReferees() {
        return referees;
    }

    public boolean isRegistrationFeePaid() {
        return registrationFeePaid;
    }

    public boolean isMembershipDuesPaid() {
        return membershipDuesPaid;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setOccupation(MemberOccupation occupation) {
        this.occupation = occupation;
    }

    public void setCollectivityIdentifier(String collectivityIdentifier) {
        this.collectivityIdentifier = collectivityIdentifier;
    }

    public void setReferees(List<String> referees) {
        this.referees = referees;
    }

    public void setRegistrationFeePaid(boolean registrationFeePaid) {
        this.registrationFeePaid = registrationFeePaid;
    }

    public void setMembershipDuesPaid(boolean membershipDuesPaid) {
        this.membershipDuesPaid = membershipDuesPaid;
    }

        @Override
    public String toString() {
        return "CreateMember{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", gender=" + gender +
                ", address='" + address + '\'' +
                ", profession='" + profession + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", occupation=" + occupation +
                ", collectivityIdentifier='" + collectivityIdentifier + '\'' +
                ", referees=" + referees +
                ", registrationFeePaid=" + registrationFeePaid +
                ", membershipDuesPaid=" + membershipDuesPaid +
                '}';
    }
}
 