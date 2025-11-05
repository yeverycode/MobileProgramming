package com.example.tomato;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeTableFragment extends Fragment {

    private TimetableView timetableView;
    private List<Schedule> scheduleList;

    private final ActivityResultLauncher<Intent> addScheduleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Schedule newSchedule = result.getData().getParcelableExtra(AddScheduleActivity.EXTRA_SCHEDULE);
                    if (newSchedule != null) {
                        if (!hasTimeConflict(newSchedule)) {
                            scheduleList.add(newSchedule);
                            timetableView.setSchedules(scheduleList);
                        } else {
                            Toast.makeText(getContext(), "해당 시간에 이미 다른 일정이 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private boolean hasTimeConflict(Schedule newSchedule) {
        for (Schedule existingSchedule : scheduleList) {
            if (existingSchedule.getDayOfWeek().equals(newSchedule.getDayOfWeek())) {
                // Check for time overlap
                if (newSchedule.getStartTime() < existingSchedule.getEndTime() && newSchedule.getEndTime() > existingSchedule.getStartTime()) {
                    return true; // Conflict found
                }
            }
        }
        return false; // No conflict
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        timetableView = view.findViewById(R.id.timetable_view);
        scheduleList = new ArrayList<>();
        
        addSampleSchedules();
        timetableView.setSchedules(scheduleList);

        FloatingActionButton fab = view.findViewById(R.id.add_schedule_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
            addScheduleLauncher.launch(intent);
        });

        return view;
    }

    private void addSampleSchedules() {
        Random rnd = new Random();
        scheduleList.add(new Schedule("소프트웨어 공학", "김철수 교수", "공학관 101호", "월", 9, 11, Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))));
        scheduleList.add(new Schedule("자료구조", "이영희 교수", "공학관 203호", "화", 13, 15, Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))));
    }
}
