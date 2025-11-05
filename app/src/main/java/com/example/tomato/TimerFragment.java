package com.example.tomato;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimerFragment extends Fragment {

    // UI Components
    private TextView timerTextView;
    private Spinner categorySpinner;
    private Button startPauseButton;
    private Button resetButton;
    private PieChart pieChart;
    private RecyclerView recordsRecyclerView;

    // Timer state and logic
    private enum TimerState { STOPPED, RUNNING, PAUSED }
    private TimerState currentState = TimerState.STOPPED;
    private long seconds = 0;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            updateTimerText();
            timerHandler.postDelayed(this, 1000);
        }
    };

    // Data
    private ArrayList<Category> categoryList;
    private ArrayList<TimerRecord> recordList;
    private Map<String, TimerRecord> recordMap;
    private TimerRecordAdapter recordAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // Initialize UI components
        timerTextView = view.findViewById(R.id.timer_text_view);
        categorySpinner = view.findViewById(R.id.timer_category_spinner);
        startPauseButton = view.findViewById(R.id.start_pause_button);
        resetButton = view.findViewById(R.id.reset_button);
        pieChart = view.findViewById(R.id.pie_chart);
        recordsRecyclerView = view.findViewById(R.id.timer_records_recycler_view);

        initializeData();
        setupUI();
        setupClickListeners();

        return view;
    }

    private void initializeData() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category("공부", R.color.category_study));
        categoryList.add(new Category("과제", R.color.category_assignment));
        categoryList.add(new Category("동아리", R.color.category_club));
        categoryList.add(new Category("휴식", R.color.category_rest));
        categoryList.add(new Category("알바", R.color.category_part_time));

        recordList = new ArrayList<>();
        recordMap = new HashMap<>();
    }

    private void setupUI() {
        // Setup Category Spinner
        ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        
        // Setup Pie Chart
        setupPieChart();

        // Setup RecyclerView
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recordAdapter = new TimerRecordAdapter(recordList);
        recordsRecyclerView.setAdapter(recordAdapter);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.getLegend().setEnabled(false);
    }

    private void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (TimerRecord record : recordList) {
            entries.add(new PieEntry(record.getDurationSeconds(), record.getCategory().getName()));
            colors.add(ContextCompat.getColor(getContext(), record.getCategory().getColorResId()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "시간 기록");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }
    
    private void setupClickListeners() {
        startPauseButton.setOnClickListener(v -> {
            if (currentState == TimerState.RUNNING) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        currentState = TimerState.RUNNING;
        startPauseButton.setText("일시정지");
        timerHandler.postDelayed(timerRunnable, 1000);
        categorySpinner.setEnabled(false);
    }

    private void pauseTimer() {
        currentState = TimerState.PAUSED;
        startPauseButton.setText("계속");
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        if (seconds > 0) {
            saveRecord();
        }

        currentState = TimerState.STOPPED;
        seconds = 0;
        timerHandler.removeCallbacks(timerRunnable);
        updateTimerText();
        startPauseButton.setText("시작");
        categorySpinner.setEnabled(true);
    }

    private void saveRecord() {
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        if (selectedCategory == null) return;

        TimerRecord record = recordMap.get(selectedCategory.getName());
        if (record == null) {
            record = new TimerRecord(selectedCategory, 0);
            recordMap.put(selectedCategory.getName(), record);
            recordList.add(record);
        }
        record.addDuration(seconds);
        recordAdapter.notifyDataSetChanged();
        updatePieChart(); // Update the pie chart with new data
    }

    private void updateTimerText() {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentState == TimerState.RUNNING) {
            pauseTimer();
        }
    }
}
