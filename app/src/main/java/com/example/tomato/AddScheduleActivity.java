package com.example.tomato;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddScheduleActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE = "com.example.tomato.SCHEDULE";
    public static final String EXTRA_DAY = "com.example.tomato.DAY";
    public static final String EXTRA_START_TIME = "com.example.tomato.START_TIME";

    private EditText courseNameEditText, professorEditText, roomEditText;
    private Spinner dayOfWeekSpinner, startTimeSpinner, endTimeSpinner;
    private Schedule existingSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        // Initialize views
        courseNameEditText = findViewById(R.id.edit_text_course_name);
        professorEditText = findViewById(R.id.edit_text_professor);
        roomEditText = findViewById(R.id.edit_text_room);
        dayOfWeekSpinner = findViewById(R.id.spinner_day_of_week);
        startTimeSpinner = findViewById(R.id.spinner_start_time);
        endTimeSpinner = findViewById(R.id.spinner_end_time);

        // Setup Spinners
        setupSpinners();

        // Handle intent extras
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SCHEDULE)) { // Editing existing schedule
            existingSchedule = intent.getParcelableExtra(EXTRA_SCHEDULE);
            setTitle("일정 수정");
            populateFieldsWithExistingSchedule();
        } else { // Adding new schedule
            setTitle("일정 추가");
            // Set initial values from Timetable click
            if (intent.hasExtra(EXTRA_DAY)) {
                setSpinnerSelection(dayOfWeekSpinner, intent.getStringExtra(EXTRA_DAY));
            }
            if (intent.hasExtra(EXTRA_START_TIME)) {
                 setSpinnerSelection(startTimeSpinner, String.format(Locale.getDefault(), "%02d:00", intent.getIntExtra(EXTRA_START_TIME, 9)));
            }
        }

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(view -> saveSchedule());
    }

    private void setupSpinners() {
        // Day of Week Spinner
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList("월", "화", "수", "목", "금"));
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(dayAdapter);

        // Time Spinners with 10-minute intervals
        List<String> timeSlots = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 10) {
                timeSlots.add(String.format(Locale.getDefault(), "%02d:%02d", h, m));
            }
        }
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(timeAdapter);
        endTimeSpinner.setAdapter(timeAdapter);
    }

    private void populateFieldsWithExistingSchedule() {
        courseNameEditText.setText(existingSchedule.getCourseName());
        professorEditText.setText(existingSchedule.getProfessor());
        roomEditText.setText(existingSchedule.getRoom());

        setSpinnerSelection(dayOfWeekSpinner, existingSchedule.getDayOfWeek());

        // Convert minutes back to HH:mm format for spinner selection
        // Note: This might not work correctly for schedules saved with the old hour-only format.
        int startTotalMinutes = existingSchedule.getStartTime();
        int startHour = startTotalMinutes / 60;
        int startMinute = startTotalMinutes % 60;
        setSpinnerSelection(startTimeSpinner, String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));

        int endTotalMinutes = existingSchedule.getEndTime();
        int endHour = endTotalMinutes / 60;
        int endMinute = endTotalMinutes % 60;
        setSpinnerSelection(endTimeSpinner, String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute));
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private void saveSchedule() {
        if (TextUtils.isEmpty(courseNameEditText.getText())) {
            Toast.makeText(this, "수업명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String courseName = courseNameEditText.getText().toString();
        String professor = professorEditText.getText().toString();
        String room = roomEditText.getText().toString();
        String day = dayOfWeekSpinner.getSelectedItem().toString();
        
        // Convert HH:mm from spinner to total minutes from midnight
        String startTimeString = startTimeSpinner.getSelectedItem().toString();
        String[] startParts = startTimeString.split(":");
        int startTimeInMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);

        String endTimeString = endTimeSpinner.getSelectedItem().toString();
        String[] endParts = endTimeString.split(":");
        int endTimeInMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);


        if(startTimeInMinutes >= endTimeInMinutes){
             Toast.makeText(this, "종료 시간은 시작 시간보다 늦어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int color;
        if (existingSchedule != null) {
            color = existingSchedule.getColor(); // Keep original color when editing
        } else {
            Random rnd = new Random();
            color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        }

        // The values for startTime and endTime are now total minutes from midnight
        Schedule schedule = new Schedule(courseName, professor, room, day, startTimeInMinutes, endTimeInMinutes, color);
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_SCHEDULE, schedule);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
