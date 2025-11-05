package com.example.tomato;

public class Todo {
    private String task;
    private boolean isCompleted;
    private int color;

    public Todo(String task, boolean isCompleted, int color) {
        this.task = task;
        this.isCompleted = isCompleted;
        this.color = color;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
