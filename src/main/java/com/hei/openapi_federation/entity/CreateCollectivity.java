package com.hei.openapi_federation.entity;

import java.util.List;


public class CreateCollectivity {
    private String location;
    private List<String> members;
    private boolean federationApproval;
    private CreateCollectivityStructure structure;


    public CreateCollectivity() {}

    public CreateCollectivity(String location, List<String> members, boolean federationApproval, CreateCollectivityStructure structure) {
        this.location = location;
        this.members = members;
        this.federationApproval = federationApproval;
        this.structure = structure;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean isFederationApproval() {
        return federationApproval;
    }

    public CreateCollectivityStructure getStructure() {
        return structure;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void setFederationApproval(boolean federationApproval) {
        this.federationApproval = federationApproval;
    }

    public void setStructure(CreateCollectivityStructure structure) {
        this.structure = structure;
    }

    @Override
    public String toString() {
        return "CreateCollectivity{" +
                "location='" + location + '\'' +
                ", members=" + members +
                ", federationApproval=" + federationApproval +
                ", structure=" + structure +
                '}';
    
}