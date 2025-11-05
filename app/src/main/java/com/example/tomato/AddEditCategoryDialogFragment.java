package com.example.tomato;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class AddEditCategoryDialogFragment extends DialogFragment {

    public interface AddEditCategoryDialogListener {
        void onCategorySaved(Category category, int position);
    }

    private AddEditCategoryDialogListener listener;
    private EditText categoryNameEditText;
    private GridLayout colorPaletteGrid;
    private Category existingCategory;
    private int editPosition = -1;
    private int selectedColorResId = -1;

    // Pre-defined list of color resources for the palette
    private static final int[] colorResources = {
        R.color.category_study, R.color.category_assignment, R.color.category_club,
        R.color.category_rest, R.color.category_part_time, R.color.tomato_red, 
        R.color.tomato_green, R.color.purple_500, R.color.purple_700, R.color.black
    };

    public static AddEditCategoryDialogFragment newInstance(@Nullable Category category, int position) {
        AddEditCategoryDialogFragment fragment = new AddEditCategoryDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("category", category);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddEditCategoryDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement AddEditCategoryDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            existingCategory = getArguments().getParcelable("category");
            editPosition = getArguments().getInt("position");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);

        categoryNameEditText = view.findViewById(R.id.edit_text_category_name);
        colorPaletteGrid = view.findViewById(R.id.color_palette_grid);

        setupColorPalette();

        if (existingCategory != null) {
            categoryNameEditText.setText(existingCategory.getName());
            selectedColorResId = existingCategory.getColorResId();
            // Highlight the selected color in the palette
        }

        builder.setView(view)
                .setTitle(existingCategory == null ? "새 카테고리 추가" : "카테고리 수정")
                .setPositiveButton("저장", (dialog, id) -> {
                    String name = categoryNameEditText.getText().toString();
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "카테고리 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(selectedColorResId == -1){
                         Toast.makeText(getContext(), "색상을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Category newCategory = new Category(name, selectedColorResId);
                    listener.onCategorySaved(newCategory, editPosition);
                })
                .setNegativeButton("취소", (dialog, id) -> getDialog().cancel());

        return builder.create();
    }

    private void setupColorPalette() {
        for (int colorRes : colorResources) {
            View colorView = new View(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) getResources().getDimension(R.dimen.color_palette_item_size);
            params.height = (int) getResources().getDimension(R.dimen.color_palette_item_size);
            params.setMargins(4, 4, 4, 4);
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
            colorView.setOnClickListener(v -> {
                selectedColorResId = colorRes;
                // Add visual feedback for selection
                 for(int i=0; i<colorPaletteGrid.getChildCount(); i++){
                    View child = colorPaletteGrid.getChildAt(i);
                    child.setAlpha(child == v ? 1.0f : 0.5f);
                }
            });
             colorView.setAlpha(0.5f);
            colorPaletteGrid.addView(colorView);
        }
    }
}
