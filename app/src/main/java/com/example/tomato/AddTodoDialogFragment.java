package com.example.tomato;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;

public class AddTodoDialogFragment extends DialogFragment {

    public interface AddTodoDialogListener {
        void onTodoAdded(Todo newTodo);
        void onManageCategoriesClicked();
    }

    private AddTodoDialogListener listener;
    private ArrayList<Category> categoryList;

    public static AddTodoDialogFragment newInstance(ArrayList<Category> categories) {
        AddTodoDialogFragment fragment = new AddTodoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("categories", categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryList = getArguments().getParcelableArrayList("categories");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddTodoDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement AddTodoDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_todo, null);

        EditText taskEditText = view.findViewById(R.id.edit_text_todo_task);
        Spinner categorySpinner = view.findViewById(R.id.spinner_category);
        Button manageCategoriesButton = view.findViewById(R.id.button_manage_categories);

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        manageCategoriesButton.setOnClickListener(v -> {
            listener.onManageCategoriesClicked();
            dismiss(); // Dismiss the current dialog
        });

        builder.setView(view)
                .setTitle("할 일 추가")
                .setPositiveButton("추가", (dialog, id) -> {
                    String task = taskEditText.getText().toString();
                    Category selectedCategory = (Category) categorySpinner.getSelectedItem();
                    if (!task.isEmpty() && selectedCategory != null) {
                        int color = getContext().getColor(selectedCategory.getColorResId());
                        Todo newTodo = new Todo(task, false, color);
                        listener.onTodoAdded(newTodo);
                    }
                })
                .setNegativeButton("취소", (dialog, id) -> getDialog().cancel());

        return builder.create();
    }
}
