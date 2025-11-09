package com.example.tomato;

import android.app.Activity;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeTableFragment extends Fragment implements TimetableView.OnTimeSlotClickListener, TimetableView.OnScheduleClickListener {

    private TimetableView timetableView;
    private List<Schedule> scheduleList;
    private int editingPosition = -1; // To track which item is being edited

    private final ActivityResultLauncher<Intent> addScheduleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Schedule returnedSchedule = result.getData().getParcelableExtra(AddScheduleActivity.EXTRA_SCHEDULE);
                    if (returnedSchedule != null) {
                        if (editingPosition >= 0) { // Existing schedule was edited
                            if (!hasTimeConflict(returnedSchedule, editingPosition)) {
                                scheduleList.set(editingPosition, returnedSchedule);
                            } else {
                                Toast.makeText(getContext(), "해당 시간에 이미 다른 일정이 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else { // New schedule is being added
                            if (!hasTimeConflict(returnedSchedule, -1)) {
                                scheduleList.add(returnedSchedule);
                            } else {
                                Toast.makeText(getContext(), "해당 시간에 이미 다른 일정이 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        timetableView.setSchedules(scheduleList);
                        editingPosition = -1; // Reset editing position
                    }
                }
            });

    private boolean hasTimeConflict(Schedule newSchedule, int ignorePosition) {
        for (int i = 0; i < scheduleList.size(); i++) {
            if (i == ignorePosition) continue; 
            
            Schedule existingSchedule = scheduleList.get(i);
            if (existingSchedule.getDayOfWeek().equals(newSchedule.getDayOfWeek())) {
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
        timetableView.setOnTimeSlotClickListener(this);
        timetableView.setOnScheduleClickListener(this);
        
        scheduleList = new ArrayList<>();
        addSampleSchedules();
        timetableView.setSchedules(scheduleList);

        FloatingActionButton fab = view.findViewById(R.id.add_schedule_fab);
        fab.setOnClickListener(v -> {
            editingPosition = -1; // Ensure we are adding, not editing
            Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
            addScheduleLauncher.launch(intent);
        });

        return view;
    }

    private void addSampleSchedules() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        // Start time 10:00 -> 600 minutes, End time 11:50 -> 710 minutes
        scheduleList.add(new Schedule("모바일프로그래밍", "윤용익", "명신관 518호", "월", 600, 710, color));
        scheduleList.add(new Schedule("모바일프로그래밍", "윤용익", "명신관 518호", "수", 600, 710, color));
    }

    @Override
    public void onTimeSlotClicked(String day, int hour) {
        editingPosition = -1; 
        Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
        intent.putExtra(AddScheduleActivity.EXTRA_DAY, day);
        intent.putExtra(AddScheduleActivity.EXTRA_START_TIME, hour);
        addScheduleLauncher.launch(intent);
    }

    @Override
    public void onScheduleClicked(Schedule schedule, int position) {
        this.editingPosition = position;
        new AlertDialog.Builder(getContext())
            .setTitle("일정 관리")
            .setItems(new CharSequence[]{"수정", "삭제"}, (dialog, which) -> {
                if (which == 0) { // 수정
                    Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                    intent.putExtra(AddScheduleActivity.EXTRA_SCHEDULE, schedule);
                    addScheduleLauncher.launch(intent);
                } else { // 삭제
                    new AlertDialog.Builder(getContext())
                        .setTitle("삭제 확인")
                        .setMessage("\'" + schedule.getCourseName() + "\' 일정을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (d, w) -> {
                            scheduleList.remove(position);
                            timetableView.setSchedules(scheduleList);
                        })
                        .setNegativeButton("취소", null)
                        .show();
                }
            })
            .setNegativeButton("취소", (dialog, which) -> editingPosition = -1) 
            .show();
    }
}
