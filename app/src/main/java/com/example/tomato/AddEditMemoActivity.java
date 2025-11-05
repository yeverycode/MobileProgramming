package com.example.tomato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditMemoActivity extends AppCompatActivity {

    public static final String EXTRA_MEMO = "com.example.tomato.EXTRA_MEMO";
    public static final String EXTRA_IS_DELETED = "com.example.tomato.IS_DELETED";

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private Button deleteButton;

    private Memo currentMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_memo);

        titleEditText = findViewById(R.id.edit_text_memo_title);
        contentEditText = findViewById(R.id.edit_text_memo_content);
        saveButton = findViewById(R.id.button_save_memo);
        deleteButton = findViewById(R.id.button_delete_memo);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_MEMO)) {
            setTitle("메모 수정");
            currentMemo = intent.getParcelableExtra(EXTRA_MEMO);
            titleEditText.setText(currentMemo.getTitle());
            contentEditText.setText(currentMemo.getContent());
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            setTitle("새 메모");
            currentMemo = new Memo("", ""); // Create a new memo object
        }

        saveButton.setOnClickListener(v -> saveMemo());
        deleteButton.setOnClickListener(v -> deleteMemo());
    }

    private void saveMemo() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (title.trim().isEmpty() && content.trim().isEmpty()) {
            Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If title is empty, use the first line of content as title
        if (title.trim().isEmpty()) {
            title = content.split("\n")[0];
        }

        currentMemo.setTitle(title);
        currentMemo.setContent(content);

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_MEMO, currentMemo);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private void deleteMemo() {
        new AlertDialog.Builder(this)
                .setTitle("메모 삭제")
                .setMessage("이 메모를 정말 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(EXTRA_IS_DELETED, true);
                    replyIntent.putExtra(EXTRA_MEMO, currentMemo); // Pass back the memo to identify it
                    setResult(RESULT_OK, replyIntent);
                    finish();
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
