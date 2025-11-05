package com.example.tomato;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ManageCategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryActionClickListener, AddEditCategoryDialogFragment.AddEditCategoryDialogListener {

    public static final String EXTRA_CATEGORIES = "com.example.tomato.CATEGORIES";

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ArrayList<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        categoryList = getIntent().getParcelableArrayListExtra(EXTRA_CATEGORIES);
        if (categoryList == null) {
            categoryList = new ArrayList<>();
        }

        recyclerView = findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.add_category_fab);
        fab.setOnClickListener(view -> {
            AddEditCategoryDialogFragment dialog = AddEditCategoryDialogFragment.newInstance(null, -1);
            dialog.show(getSupportFragmentManager(), "AddEditCategoryDialog");
        });
    }

    @Override
    public void onEditClick(int position) {
        Category categoryToEdit = categoryList.get(position);
        AddEditCategoryDialogFragment dialog = AddEditCategoryDialogFragment.newInstance(categoryToEdit, position);
        dialog.show(getSupportFragmentManager(), "AddEditCategoryDialog");
    }

    @Override
    public void onDeleteClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("카테고리 삭제")
                .setMessage("이 카테고리를 삭제하시겠습니까? 관련된 모든 할 일의 카테고리가 초기화됩니다.")
                .setPositiveButton("삭제", (dialog, which) -> {
                    categoryList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    @Override
    public void onCategorySaved(Category category, int position) {
        if (position == -1) { // New category
            categoryList.add(category);
            adapter.notifyItemInserted(categoryList.size() - 1);
        } else { // Existing category
            categoryList.set(position, category);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra(EXTRA_CATEGORIES, categoryList);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
