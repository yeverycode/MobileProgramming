package com.example.tomato;

import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// NOTE: This adapter is now designed to handle a List<Object>,
// which can contain both Todo items and String items (for category dividers).
// The Fragment using this adapter will be responsible for creating this mixed list.
public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TODO = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;

    private List<Object> items;
    private final OnTodoCheckedChangeListener listener;

    public interface OnTodoCheckedChangeListener {
        void onTodoCheckedChanged(Todo todo, boolean isChecked);
    }

    public TodoAdapter(List<Object> items, OnTodoCheckedChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_DIVIDER;
        }
        return VIEW_TYPE_TODO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_DIVIDER) {
            View view = inflater.inflate(R.layout.list_item_divider, parent, false);
            return new DividerViewHolder(view);
        }
        // else, viewType is VIEW_TYPE_TODO
        View view = inflater.inflate(R.layout.list_item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_DIVIDER) {
            String categoryName = (String) items.get(position);
            DividerViewHolder dividerHolder = (DividerViewHolder) holder;
            dividerHolder.dividerTextView.setText(categoryName);
        } else {
            Todo todo = (Todo) items.get(position);
            TodoViewHolder todoHolder = (TodoViewHolder) holder;

            todoHolder.taskTextView.setText(todo.getTask());

            // Set color indicator from Todo's color property
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.OVAL);
            gradientDrawable.setColor(todo.getColor()); // Assumes todo.getColor() returns an int color
            todoHolder.colorView.setBackground(gradientDrawable);

            // Set checkbox and strikethrough state
            updateStrikeThrough(todoHolder.taskTextView, todo.isCompleted());
            todoHolder.checkBox.setOnCheckedChangeListener(null);
            todoHolder.checkBox.setChecked(todo.isCompleted());
            todoHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                todo.setCompleted(isChecked);
                updateStrikeThrough(todoHolder.taskTextView, isChecked);
                if (listener != null) {
                    listener.onTodoCheckedChanged(todo, isChecked);
                }
            });
        }
    }

    private void updateStrikeThrough(TextView textView, boolean isCompleted) {
        if (isCompleted) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder for the Divider
    static class DividerViewHolder extends RecyclerView.ViewHolder {
        TextView dividerTextView;
        public DividerViewHolder(@NonNull View itemView) {
            super(itemView);
            dividerTextView = itemView.findViewById(R.id.divider_text);
        }
    }

    // ViewHolder for the Todo item
    static class TodoViewHolder extends RecyclerView.ViewHolder {
        View colorView;
        TextView taskTextView;
        CheckBox checkBox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.todo_color_view);
            taskTextView = itemView.findViewById(R.id.todo_task_text_view);
            checkBox = itemView.findViewById(R.id.todo_checkbox);
        }
    }
}
