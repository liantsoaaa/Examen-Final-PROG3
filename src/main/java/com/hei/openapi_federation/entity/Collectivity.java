package com.hei.openapi_federation.entity;

import java.util.List;

public class Collectivity {
    private String id;
    private String location;
    private CollectivityStructure structure;
    private List<String> members;

    public Collectivity() {}

    public Collectivity(String id, String location, CollectivityStructure structure, List<String> members) {
        this.id = id;
        this.location = location;
        this.structure = structure;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public CollectivityStructure getStructure() {
        return structure;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStructure(CollectivityStructure structure) {
        this.structure = structure;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {        return "Collectivity{" +
                "id='" + id + '\'' +
                ", location='" + location + '\'' +
                ", structure=" + structure +
                ", members=" + members + 
                '}';    

    
}