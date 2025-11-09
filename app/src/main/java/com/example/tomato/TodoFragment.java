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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TodoFragment extends Fragment implements AddTodoDialogFragment.AddTodoDialogListener, TodoAdapter.OnTodoCheckedChangeListener {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private ArrayList<Todo> todoList; // Master list of todos
    private ArrayList<Category> categoryList;
    private List<Object> itemsForAdapter; // List passed to the adapter

    private final ActivityResultLauncher<Intent> manageCategoriesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<Category> updatedCategories = result.getData().getParcelableArrayListExtra(ManageCategoriesActivity.EXTRA_CATEGORIES);
                    if (updatedCategories != null) {
                        this.categoryList = updatedCategories;
                        // When categories change, the list needs to be rebuilt
                        refreshAdapterList();
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

        itemsForAdapter = new ArrayList<>();
        adapter = new TodoAdapter(itemsForAdapter, this);
        recyclerView.setAdapter(adapter);

        refreshAdapterList();

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
        if(getContext() != null) {
            todoList.add(new Todo("알고리즘 문제 풀기", false, ContextCompat.getColor(getContext(), R.color.category_study)));
            todoList.add(new Todo("운영체제 과제", false, ContextCompat.getColor(getContext(), R.color.category_assignment)));
            todoList.add(new Todo("UMC 스터디", true, ContextCompat.getColor(getContext(), R.color.category_club)));
            todoList.add(new Todo("소프트웨어 공학 복습", false, ContextCompat.getColor(getContext(), R.color.category_study)));
        }
    }

    private void refreshAdapterList() {
        if (todoList == null || categoryList == null || getContext() == null) {
            return;
        }

        // Group todos by their category name. Use LinkedHashMap to preserve the initial category order.
        Map<String, List<Todo>> todosByCategory = new LinkedHashMap<>();
        for (Category category : categoryList) {
            todosByCategory.put(category.getName(), new ArrayList<>());
        }

        Map<Integer, String> colorToNameMap = new LinkedHashMap<>();
        for (Category category : categoryList) {
            colorToNameMap.put(ContextCompat.getColor(getContext(), category.getColorResId()), category.getName());
        }

        for (Todo todo : todoList) {
            String categoryName = colorToNameMap.get(todo.getColor());
            if (categoryName != null && todosByCategory.containsKey(categoryName)) {
                todosByCategory.get(categoryName).add(todo);
            }
        }

        itemsForAdapter.clear();
        // Iterate through the grouped map and add dividers and items to the adapter's list.
        for (Map.Entry<String, List<Todo>> entry : todosByCategory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                itemsForAdapter.add(entry.getKey()); // Add category name as a divider
                itemsForAdapter.addAll(entry.getValue()); // Add all todos for that category
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showAddTodoDialog(){
        AddTodoDialogFragment dialogFragment = AddTodoDialogFragment.newInstance(categoryList);
        dialogFragment.show(getParentFragmentManager(), "AddTodoDialogFragment");
    }

    @Override
    public void onTodoAdded(Todo newTodo) {
        todoList.add(newTodo);
        refreshAdapterList();
    }

    @Override
    public void onManageCategoriesClicked() {
        Intent intent = new Intent(getActivity(), ManageCategoriesActivity.class);
        intent.putParcelableArrayListExtra(ManageCategoriesActivity.EXTRA_CATEGORIES, categoryList);
        manageCategoriesLauncher.launch(intent);
    }

    @Override
    public void onTodoCheckedChanged(Todo todo, boolean isChecked) {
        // Here you can add logic to persist the state, e.g., save to a database.
    }
}
