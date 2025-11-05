package com.example.tomato;

public class TimerRecord {
    private Category category;
    private long durationSeconds;

    public TimerRecord(Category category, long durationSeconds) {
        this.category = category;
        this.durationSeconds = durationSeconds;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void addDuration(long seconds) {
        this.durationSeconds += seconds;
    }
}
