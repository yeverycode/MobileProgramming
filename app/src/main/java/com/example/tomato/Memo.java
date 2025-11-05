package com.example.tomato;

import android.os.Parcel;
import android.os.Parcelable;

public class Memo implements Parcelable {
    private long id;
    private String title;
    private String content;
    private long timestamp;

    public Memo(String title, String content) {
        this.id = System.currentTimeMillis(); // Use timestamp as a simple unique ID
        this.title = title;
        this.content = content;
        this.timestamp = this.id;
    }

    // Getters and Setters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Parcelable implementation
    protected Memo(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(Parcel in) {
            return new Memo(in);
        }

        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(timestamp);
    }
}
