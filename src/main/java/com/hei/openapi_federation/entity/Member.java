package com.hei.openapi_federation.entity;

public class Member {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String adresse;
    private String profession;
    private int phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private String collectivityId;

    public Member(String firstName, String lastName, LocalDate birthDate, Gender gender, String adresse, String profession, int phoneNumber, String email, MemberOccupation occupation, String collectivityId) {
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
    }

}