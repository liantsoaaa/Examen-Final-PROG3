package com.hei.openapi_federation.entity;

import java.time.LocalDateTime;

public class MemberCollectivity {
    private int id;
    private int idMember;
    private int idCollectivity;
    private String postName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public MemberCollectivity() {
    }

    public MemberCollectivity(int id, int idMember, int idCollectivity, String postName, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.idMember = idMember;
        this.idCollectivity = idCollectivity;
        this.postName = postName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public int getIdMember() {
        return idMember;
    }

    public int getIdCollectivity() {
        return idCollectivity;
    }

    public String getPostName() {
        return postName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdMember(int idMember) {
        this.idMember = idMember;
    }

    public void setIdCollectivity(int idCollectivity) {
        this.idCollectivity = idCollectivity;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "MemberCollectivity{" +
                "id=" + id +
                ", idMember=" + idMember +
                ", idCollectivity=" + idCollectivity +
                ", postName='" + postName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';

    }
}