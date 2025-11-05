package com.example.tomato;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.courseNameTextView.setText(schedule.getCourseName());
        holder.professorTextView.setText(schedule.getProfessor());
        holder.roomTextView.setText(schedule.getRoom());

        // Set background color with rounded corners
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(schedule.getColor());
        gradientDrawable.setCornerRadius(16); // Adjust corner radius as needed
        holder.scheduleItemLayout.setBackground(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView;
        TextView professorTextView;
        TextView roomTextView;
        LinearLayout scheduleItemLayout;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.course_name_text_view);
            professorTextView = itemView.findViewById(R.id.professor_text_view);
            roomTextView = itemView.findViewById(R.id.room_text_view);
            scheduleItemLayout = itemView.findViewById(R.id.schedule_item_layout);
        }
    }
}
