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

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> todoList;

    public TodoAdapter(List<Todo> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.taskTextView.setText(todo.getTask());
        holder.checkBox.setChecked(todo.isCompleted());

        // Set color indicator
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(todo.getColor());
        holder.colorView.setBackground(gradientDrawable);

        updateStrikeThrough(holder.taskTextView, todo.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setCompleted(isChecked);
            updateStrikeThrough(holder.taskTextView, isChecked);
        });
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
        return todoList.size();
    }

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
