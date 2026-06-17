package com.mani.affordmed.dto;

public class Vehicle {
    private String taskId;
    private Integer duration;
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
