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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MemoFragment extends Fragment implements MemoAdapter.OnMemoClickListener {

    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private List<Memo> memoList;

    private final ActivityResultLauncher<Intent> memoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Memo returnedMemo = result.getData().getParcelableExtra(AddEditMemoActivity.EXTRA_MEMO);
                    boolean isDeleted = result.getData().getBooleanExtra(AddEditMemoActivity.EXTRA_IS_DELETED, false);

                    if (returnedMemo != null) {
                        if (isDeleted) {
                            removeMemoById(returnedMemo.getId());
                        } else {
                            updateOrAddMemo(returnedMemo);
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        recyclerView = view.findViewById(R.id.memo_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        memoList = new ArrayList<>();
        addSampleMemos();

        adapter = new MemoAdapter(memoList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.add_memo_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditMemoActivity.class);
            memoLauncher.launch(intent);
        });

        return view;
    }

    private void addSampleMemos() {
        memoList.add(new Memo("첫 번째 메모", "환영합니다! 이것은 샘플 메모입니다."));
        memoList.add(new Memo("아이디어", "여기에 떠오르는 아이디어를 기록하세요."));
    }

    @Override
    public void onMemoClick(int position) {
        Intent intent = new Intent(getActivity(), AddEditMemoActivity.class);
        intent.putExtra(AddEditMemoActivity.EXTRA_MEMO, memoList.get(position));
        memoLauncher.launch(intent);
    }

    private void updateOrAddMemo(Memo memo) {
        for (int i = 0; i < memoList.size(); i++) {
            if (memoList.get(i).getId() == memo.getId()) {
                memoList.set(i, memo);
                adapter.notifyItemChanged(i);
                return;
            }
        }
        // If not found, add as a new memo
        memoList.add(0, memo); // Add to the top
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
    }

    private void removeMemoById(long memoId) {
        for (int i = 0; i < memoList.size(); i++) {
            if (memoList.get(i).getId() == memoId) {
                memoList.remove(i);
                adapter.notifyItemRemoved(i);
                return;
            }
        }
    }
}
