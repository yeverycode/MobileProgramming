package com.example.tomato;

import android.os.Parcel;
import android.os.Parcelable;

public class Schedule implements Parcelable {
    private String courseName;
    private String professor;
    private String room;
    private String dayOfWeek;
    private int startTime;
    private int endTime;
    private int color;

    public Schedule(String courseName, String professor, String room, String dayOfWeek, int startTime, int endTime, int color) {
        this.courseName = courseName;
        this.professor = professor;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }

    protected Schedule(Parcel in) {
        courseName = in.readString();
        professor = in.readString();
        room = in.readString();
        dayOfWeek = in.readString();
        startTime = in.readInt();
        endTime = in.readInt();
        color = in.readInt();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    // Getters and Setters
    public String getCourseName() { return courseName; }
    public String getProfessor() { return professor; }
    public String getRoom() { return room; }
    public String getDayOfWeek() { return dayOfWeek; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
    public int getColor() { return color; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(professor);
        dest.writeString(room);
        dest.writeString(dayOfWeek);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
        dest.writeInt(color);
    }
}
