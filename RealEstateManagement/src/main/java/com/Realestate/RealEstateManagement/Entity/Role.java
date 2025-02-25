package com.Realestate.RealEstateManagement.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    BUYER, SELLER, AGENT, ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + value + ". Allowed roles: ADMIN, AGENT, BUYER, SELLER");
        }
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
