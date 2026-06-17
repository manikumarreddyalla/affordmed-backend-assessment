package com.mani.affordmed.dto;

import java.util.List;

public class SchedulerResult {
    private List<Vehicle> selectedTasks;
    private int totalImpact;

    public SchedulerResult() {
    }

    public SchedulerResult(List<Vehicle> selectedTasks, int totalImpact) {
        this.selectedTasks = selectedTasks;
        this.totalImpact = totalImpact;
    }

    public List<Vehicle> getSelectedTasks() {
        return selectedTasks;
    }

    public void setSelectedTasks(List<Vehicle> selectedTasks) {
        this.selectedTasks = selectedTasks;
    }

    public int getTotalImpact() {
        return totalImpact;
    }

    public void setTotalImpact(int totalImpact) {
        this.totalImpact = totalImpact;
    }
}
