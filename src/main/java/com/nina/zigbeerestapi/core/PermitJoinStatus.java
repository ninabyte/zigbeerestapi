package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class PermitJoinStatus {
    private long id;

    private boolean status;

    public PermitJoinStatus() {
        // Jackson deserialization
    }

    public PermitJoinStatus(long id, boolean status) {
        this.id = id;
        this.status = status;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public boolean getStatus() {
        return status;
    }
}