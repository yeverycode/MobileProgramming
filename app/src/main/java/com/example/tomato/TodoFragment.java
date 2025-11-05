package com.example.tomato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoFragment extends Fragment implements AddTodoDialogFragment.AddTodoDialogListener {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private ArrayList<Todo> todoList;
    private ArrayList<Category> categoryList;

    private final ActivityResultLauncher<Intent> manageCategoriesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<Category> updatedCategories = result.getData().getParcelableArrayListExtra(ManageCategoriesActivity.EXTRA_CATEGORIES);
                    if (updatedCategories != null) {
                        this.categoryList = updatedCategories;
                        // Optionally, re-show the add dialog or just update for next time
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        recyclerView = view.findViewById(R.id.todo_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeData();

        adapter = new TodoAdapter(todoList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.add_todo_fab);
        fab.setOnClickListener(v -> showAddTodoDialog());

        return view;
    }

    private void initializeData() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category("공부", R.color.category_study));
        categoryList.add(new Category("과제", R.color.category_assignment));
        categoryList.add(new Category("동아리", R.color.category_club));
        categoryList.add(new Category("휴식", R.color.category_rest));
        categoryList.add(new Category("알바", R.color.category_part_time));

        todoList = new ArrayList<>();
        todoList.add(new Todo("알고리즘 문제 풀기", false, ContextCompat.getColor(getContext(), R.color.category_study)));
        todoList.add(new Todo("운영체제 과제", false, ContextCompat.getColor(getContext(), R.color.category_assignment)));
        todoList.add(new Todo("UMC 스터디", true, ContextCompat.getColor(getContext(), R.color.category_club)));
    }
    
    private void showAddTodoDialog(){
        AddTodoDialogFragment dialogFragment = AddTodoDialogFragment.newInstance(categoryList);
        dialogFragment.show(getParentFragmentManager(), "AddTodoDialogFragment");
    }

    @Override
    public void onTodoAdded(Todo newTodo) {
        todoList.add(newTodo);
        adapter.notifyItemInserted(todoList.size() - 1);
    }

    @Override
    public void onManageCategoriesClicked() {
        Intent intent = new Intent(getActivity(), ManageCategoriesActivity.class);
        intent.putParcelableArrayListExtra(ManageCategoriesActivity.EXTRA_CATEGORIES, categoryList);
        manageCategoriesLauncher.launch(intent);
    }
}
