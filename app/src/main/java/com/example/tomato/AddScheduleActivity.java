package com.example.tomato;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class AddScheduleActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE = "com.example.tomato.SCHEDULE";

    private EditText courseNameEditText;
    private EditText professorEditText;
    private EditText roomEditText;
    private EditText dayOfWeekEditText;
    private EditText startTimeEditText;
    private EditText endTimeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        courseNameEditText = findViewById(R.id.edit_text_course_name);
        professorEditText = findViewById(R.id.edit_text_professor);
        roomEditText = findViewById(R.id.edit_text_room);
        dayOfWeekEditText = findViewById(R.id.edit_text_day_of_week);
        startTimeEditText = findViewById(R.id.edit_text_start_time);
        endTimeEditText = findViewById(R.id.edit_text_end_time);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(courseNameEditText.getText()) ||
                        TextUtils.isEmpty(dayOfWeekEditText.getText()) ||
                        TextUtils.isEmpty(startTimeEditText.getText()) ||
                        TextUtils.isEmpty(endTimeEditText.getText())) {
                    Toast.makeText(AddScheduleActivity.this, "필수 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String courseName = courseNameEditText.getText().toString();
                    String professor = professorEditText.getText().toString();
                    String room = roomEditText.getText().toString();
                    String day = dayOfWeekEditText.getText().toString();
                    int startTime = Integer.parseInt(startTimeEditText.getText().toString());
                    int endTime = Integer.parseInt(endTimeEditText.getText().toString());

                    // Generate a random color for the schedule item
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                    Schedule schedule = new Schedule(courseName, professor, room, day, startTime, endTime, color);
                    replyIntent.putExtra(EXTRA_SCHEDULE, schedule);
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            }
        });
    }
}