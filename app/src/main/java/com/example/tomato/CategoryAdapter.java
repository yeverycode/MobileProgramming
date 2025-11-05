package com.example.tomato;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryActionClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    private List<Category> categoryList;
    private OnCategoryActionClickListener listener;

    public CategoryAdapter(List<Category> categoryList, OnCategoryActionClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getName());

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), category.getColorResId()));
        holder.categoryColorView.setBackground(gradientDrawable);

        holder.editButton.setOnClickListener(v -> listener.onEditClick(holder.getAdapterPosition()));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        View categoryColorView;
        TextView categoryNameTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryColorView = itemView.findViewById(R.id.category_color_view);
            categoryNameTextView = itemView.findViewById(R.id.category_name_text_view);
            editButton = itemView.findViewById(R.id.button_edit_category);
            deleteButton = itemView.findViewById(R.id.button_delete_category);
        }
    }
}
