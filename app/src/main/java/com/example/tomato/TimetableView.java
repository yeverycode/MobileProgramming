package com.example.tomato;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TimetableView extends View {

    private final Paint gridPaint = new Paint();
    private final Paint timeTextPaint = new Paint();
    private final Paint schedulePaint = new Paint();
    private final Paint scheduleTextPaint = new Paint();

    private int numDays = 5; // Mon-Fri
    private int startHour = 9;
    private int endHour = 18;
    private float headerHeight = 100f;
    private float timeColumnWidth = 150f;
    
    private float cellHeight = 200f; // Height for one hour
    private float cellWidth;

    private List<Schedule> schedules = new ArrayList<>();

    private String[] dayHeaders = {"월", "화", "수", "목", "금"};

    public TimetableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
    
    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        invalidate(); // Redraw the view
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

        // Horizontal lines for hours
        for (int i = startHour; i <= endHour; i++) {
            float y = headerHeight + (i - startHour) * cellHeight;
            canvas.drawLine(timeColumnWidth, y, getWidth(), y, gridPaint);
        }
        
        // Header separator line
        canvas.drawLine(0, headerHeight, getWidth(), headerHeight, gridPaint);
        // Time column separator line
        canvas.drawLine(timeColumnWidth, 0, timeColumnWidth, getHeight(), gridPaint);
    }
    
    private void drawDayHeaders(Canvas canvas) {
        for (int i = 0; i < numDays; i++) {
            float x = timeColumnWidth + i * cellWidth + cellWidth / 2;
            canvas.drawText(dayHeaders[i], x, headerHeight / 2 + 15, timeTextPaint);
        }
    }

    private void drawTimeLabels(Canvas canvas) {
        for (int i = startHour; i <= endHour; i++) {
            float y = headerHeight + (i - startHour) * cellHeight - (cellHeight / 2) + 15;
            canvas.drawText(String.valueOf(i), timeColumnWidth / 2, y, timeTextPaint);
        }
    }

    private void drawSchedules(Canvas canvas) {
        for (Schedule schedule : schedules) {
            int dayIndex = getDayIndex(schedule.getDayOfWeek());
            if (dayIndex < 0) continue;

            float left = timeColumnWidth + dayIndex * cellWidth + 5;
            float top = headerHeight + (schedule.getStartTime() - startHour) * cellHeight + 5;
            float right = left + cellWidth - 10;
            float bottom = headerHeight + (schedule.getEndTime() - startHour) * cellHeight - 5;

            schedulePaint.setColor(schedule.getColor());
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, 15f, 15f, schedulePaint);

            // Draw text inside the schedule block
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
        // Make the view scrollable by setting a large enough height for the time range
        int height = (int) (headerHeight + (endHour - startHour) * cellHeight);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }
}
