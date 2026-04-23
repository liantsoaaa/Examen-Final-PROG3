package com.hei.openapi_federation.entity;

import java.time.LocalDateTime;

public class Sponsorship {
    private int id;
    private int idCandidate;
    private int idSponsor;
    private int idCollectivity;
    private String relationship;
    private LocalDateTime createdAt;

    public Sponsorship() {
    }

    public Sponsorship(int id, int idCandidate, int idSponsor, int idCollectivity, String relationship, LocalDateTime createdAt) {
        this.id = id;
        this.idCandidate = idCandidate;
        this.idSponsor = idSponsor;
        this.idCollectivity = idCollectivity;
        this.relationship = relationship;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getIdCandidate() {
        return idCandidate;
    }

    public int getIdSponsor() {
        return idSponsor;
    }

    public int getIdCollectivity() {
        return idCollectivity;
    }

    public String getRelationship() {
        return relationship;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdCandidate(int idCandidate) {
        this.idCandidate = idCandidate;
    }

    public void setIdSponsor(int idSponsor) {
        this.idSponsor = idSponsor;
    }

    public void setIdCollectivity(int idCollectivity) {
        this.idCollectivity = idCollectivity;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Sponsorship{" +
                "id=" + id +
                ", idCandidate=" + idCandidate +
                ", idSponsor=" + idSponsor +
                ", idCollectivity=" + idCollectivity +
                ", relationship='" + relationship + '\'' +
                ", createdAt=" + createdAt +
                '}';

    }
}