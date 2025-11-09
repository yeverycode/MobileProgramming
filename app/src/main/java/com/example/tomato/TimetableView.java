package com.example.tomato;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimetableView extends View {

    // Listeners
    public interface OnTimeSlotClickListener {
        void onTimeSlotClicked(String day, int hour);
    }
    public interface OnScheduleClickListener {
        void onScheduleClicked(Schedule schedule, int position);
    }

    private OnTimeSlotClickListener timeSlotClickListener;
    private OnScheduleClickListener scheduleClickListener;

    private final Paint gridPaint = new Paint();
    private final Paint timeTextPaint = new Paint();
    private final Paint schedulePaint = new Paint();
    private final Paint scheduleTextPaint = new Paint();

    private int numDays = 5; // Mon-Fri
    private int startHour = 9;
    private int endHour = 18;
    private float headerHeight = 100f;
    private float timeColumnWidth = 150f;
    
    // cellHeight now represents the height of a 10-minute interval.
    private float cellHeight = 35f; 
    private float cellWidth;

    private List<Schedule> schedules = new ArrayList<>();
    private String[] dayHeaders = {"월", "화", "수", "목", "금"};

    public TimetableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // Paint initializations
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);
        timeTextPaint.setColor(Color.DKGRAY);
        timeTextPaint.setTextSize(40f);
        timeTextPaint.setTextAlign(Paint.Align.CENTER);
        schedulePaint.setStyle(Paint.Style.FILL);
        scheduleTextPaint.setColor(Color.WHITE);
        scheduleTextPaint.setTextSize(35f);
        scheduleTextPaint.setTextAlign(Paint.Align.LEFT);
    }
    
    public void setOnTimeSlotClickListener(OnTimeSlotClickListener listener) {
        this.timeSlotClickListener = listener;
    }

    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.scheduleClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();

            // First, check if a schedule was clicked
            if (scheduleClickListener != null) {
                for (int i = 0; i < schedules.size(); i++) {
                    Schedule schedule = schedules.get(i);
                    int dayIndex = getDayIndex(schedule.getDayOfWeek());
                    if (dayIndex < 0) continue;
                    
                    float top = headerHeight + ((schedule.getStartTime() - startHour * 60) / 10f) * cellHeight;
                    float bottom = headerHeight + ((schedule.getEndTime() - startHour * 60) / 10f) * cellHeight;
                    float left = timeColumnWidth + dayIndex * cellWidth;
                    float right = left + cellWidth;

                    if (x >= left && x <= right && y >= top && y <= bottom) {
                        scheduleClickListener.onScheduleClicked(schedule, i);
                        return true; // Event handled
                    }
                }
            }

            // If no schedule was clicked, check if an empty time slot was clicked
            if (timeSlotClickListener != null && x > timeColumnWidth && y > headerHeight) {
                int dayIndex = (int) ((x - timeColumnWidth) / cellWidth);
                int hour = startHour + (int) ((y - headerHeight) / (cellHeight * 6)); // 6 cells per hour

                if (dayIndex < dayHeaders.length && hour < endHour) {
                    timeSlotClickListener.onTimeSlotClicked(dayHeaders[dayIndex], hour);
                    return true; // Event handled
                }
            }
        }
        return true; 
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellWidth = (w - timeColumnWidth) / numDays;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawDayHeaders(canvas);
        drawTimeLabels(canvas);
        drawSchedules(canvas);
    }

    private void drawGrid(Canvas canvas) {
        // Vertical lines for days
        for (int i = 1; i < numDays; i++) {
            float x = timeColumnWidth + i * cellWidth;
            canvas.drawLine(x, 0, x, getHeight(), gridPaint);
        }
        // Horizontal lines for hours (1-hour intervals)
        for (int i = startHour; i <= endHour; i++) {
            float y = headerHeight + ((i - startHour) * 60 / 10f) * cellHeight;
            canvas.drawLine(timeColumnWidth, y, getWidth(), y, gridPaint);
        }
        canvas.drawLine(0, headerHeight, getWidth(), headerHeight, gridPaint);
        canvas.drawLine(timeColumnWidth, 0, timeColumnWidth, getHeight(), gridPaint);
    }
    
    private void drawDayHeaders(Canvas canvas) {
        for (int i = 0; i < numDays; i++) {
            float x = timeColumnWidth + i * cellWidth + cellWidth / 2;
            canvas.drawText(dayHeaders[i], x, headerHeight / 2 + 15, timeTextPaint);
        }
    }

    private void drawTimeLabels(Canvas canvas) {
        // Draw labels for each hour
        for (int i = startHour; i <= endHour; i++) {
            float y = headerHeight + ((i - startHour) * 60 / 10f) * cellHeight + timeTextPaint.getTextSize();
            canvas.drawText(String.format(Locale.getDefault(), "%02d:00", i), timeColumnWidth / 2, y, timeTextPaint);
        }
    }

    private void drawSchedules(Canvas canvas) {
        for (Schedule schedule : schedules) {
            int dayIndex = getDayIndex(schedule.getDayOfWeek());
            if (dayIndex < 0) continue;

            // Calculate top and bottom based on minutes from startHour
            float top = headerHeight + ((schedule.getStartTime() - startHour * 60) / 10f) * cellHeight + 5;
            float bottom = headerHeight + ((schedule.getEndTime() - startHour * 60) / 10f) * cellHeight - 5;

            float left = timeColumnWidth + dayIndex * cellWidth + 5;
            float right = left + cellWidth - 10;

            schedulePaint.setColor(schedule.getColor());
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, 15f, 15f, schedulePaint);

            canvas.save();
            canvas.clipRect(rect);
            canvas.drawText(schedule.getCourseName(), left + 20, top + 50, scheduleTextPaint);
            if(schedule.getProfessor() != null && !schedule.getProfessor().isEmpty()) {
                 canvas.drawText(schedule.getProfessor(), left + 20, top + 100, scheduleTextPaint);
            }
            if(schedule.getRoom() != null && !schedule.getRoom().isEmpty()) {
                canvas.drawText(schedule.getRoom(), left + 20, top + 150, scheduleTextPaint);
            }
            canvas.restore();
        }
    }

    private int getDayIndex(String day) {
        for(int i=0; i<dayHeaders.length; i++){
            if(dayHeaders[i].equals(day)) return i;
        }
        return -1;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate total height based on 10-minute intervals
        int totalMinutes = (endHour - startHour) * 60;
        int numCells = totalMinutes / 10;
        int height = (int) (headerHeight + numCells * cellHeight);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }
}
