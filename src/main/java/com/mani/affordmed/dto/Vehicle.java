package com.mani.affordmed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Vehicle {
    @JsonProperty("TaskID")
    private String taskId;

    @JsonProperty("Duration")
    private Integer duration;

    @JsonProperty("Impact")
    private Integer impact;

    public Vehicle() {
    }

    public Vehicle(String taskId, Integer duration, Integer impact) {
        this.taskId = taskId;
        this.duration = duration;
        this.impact = impact;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getImpact() {
        return impact;
    }

    public void setImpact(Integer impact) {
        this.impact = impact;
    }
}
