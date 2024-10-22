package model;

// Package: com.taskmanager.model

public class Task {
    private String taskName;
    private String description;
    private String category;
    private String startDate;
    private String deadline;
    private String status;

    public Task(String taskName, String description, String category, String startDate, String deadline, String status) {
        this.taskName = taskName;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }
}
