package com.hei.openapi_federation.entity;

public class CreateCollectivityStructure {

    private String president;
    private String vicePresident;
    private String secretary;
    private String treasurer;

    public CreateCollectivityStructure() {}

    public CreateCollectivityStructure(String president, String vicePresident, String secretary, String treasurer) {
        this.president = president;
        this.vicePresident = vicePresident;
        this.secretary = secretary;
        this.treasurer = treasurer;
    }

    public String getPresident() {
        return president;
    }

    public void setPresident(String president) {
        this.president = president;
    }

    public String getVicePresident() {
        return vicePresident;
    }

    public void setVicePresident(String vicePresident) {
        this.vicePresident = vicePresident;
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
    }

    public String getTreasurer() {
        return treasurer;
    }

    public void setTreasurer(String treasurer) {
        this.treasurer = treasurer;
    }
    
}