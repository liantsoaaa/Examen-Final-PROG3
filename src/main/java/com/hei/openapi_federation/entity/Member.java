package com.hei.openapi_federation.entity;

import java.time.LocalDate;
import java.util.List;

public class Member {
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String adress;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private int collectivityId;
    private List<Member> referees;

    public Member() {}

    public Member(int id, String firstName, String lastName, LocalDate birthDate, Gender gender, String adresse, String profession, int phoneNumber, String email, MemberOccupation occupation, String collectivityId, List<Member> referees) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.adresse = adresse;
        this.profession = profession;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.occupation = occupation;
        this.collectivityId = collectivityId;
        this.referees = referees;
    }

    public int getId() {
        return id;
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

    public String getAdress() {
        return adress;
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

    public String getCollectivityId() {
        return collectivityId;
    }

    public List<Member> getReferees() {
        return referees;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setGender  (Gender  gender) {
        this.gender = gender;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOccupation(MemberOccupation occupation) {
        this.occupation = occupation;
    }

    public void setCollectivityId(String collectivityId) {
        this.collectivityId = collectivityId;
    }

    public void setReferees(List<Member> referees) {
        this.referees = referees;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", gender=" + gender +
                ", adress='" + adress + '\'' +
                ", profession='" + profession + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", occupation=" + occupation +
                ", collectivityId='" + collectivityId + '\'' +
                '}';
}