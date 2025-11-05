package com.example.tomato;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TimerRecordAdapter extends RecyclerView.Adapter<TimerRecordAdapter.TimerRecordViewHolder> {

    private final List<TimerRecord> timerRecords;

    public TimerRecordAdapter(List<TimerRecord> timerRecords) {
        this.timerRecords = timerRecords;
    }

    @NonNull
    @Override
    public TimerRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_timer_record, parent, false);
        return new TimerRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerRecordViewHolder holder, int position) {
        TimerRecord record = timerRecords.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return timerRecords.size();
    }

    static class TimerRecordViewHolder extends RecyclerView.ViewHolder {
        private final View colorView;
        private final TextView categoryNameTextView;
        private final TextView durationTextView;

        public TimerRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.record_color_view);
            categoryNameTextView = itemView.findViewById(R.id.record_category_name_text_view);
            durationTextView = itemView.findViewById(R.id.record_duration_text_view);
        }

        public void bind(TimerRecord record) {
            Context context = itemView.getContext();
            categoryNameTextView.setText(record.getCategory().getName());

            // Set color indicator
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.OVAL);
            gradientDrawable.setColor(ContextCompat.getColor(context, record.getCategory().getColorResId()));
            colorView.setBackground(gradientDrawable);

            // Format duration from seconds to HH:mm:ss
            long hours = record.getDurationSeconds() / 3600;
            long minutes = (record.getDurationSeconds() % 3600) / 60;
            long seconds = record.getDurationSeconds() % 60;
            durationTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
        }
    }
}
