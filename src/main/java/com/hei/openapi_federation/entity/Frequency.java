package com.hei.openapi_federation.entity;

public enum Frequency {
    WEEKLY,
    MONTHLY,
    ANNUALLY,
    PUNCTUALLY;

    public String toDbFrequency() {
        return this.name();
    }
}
