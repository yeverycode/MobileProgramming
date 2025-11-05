package com.example.tomato;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private String name;
    private int colorResId;

    public Category(String name, int colorResId) {
        this.name = name;
        this.colorResId = colorResId;
    }

    protected Category(Parcel in) {
        name = in.readString();
        colorResId = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }

    // This is important for the Spinner to display the category name
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(colorResId);
    }
}
