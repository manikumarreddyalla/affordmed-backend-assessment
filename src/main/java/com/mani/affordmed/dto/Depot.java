package com.mani.affordmed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Depot {
    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("MechanicHours")
    private Integer mechanicHours;

    public Depot() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMechanicHours() {
        return mechanicHours;
    }

    public void setMechanicHours(Integer mechanicHours) {
        this.mechanicHours = mechanicHours;
    }
}
