package com.example.tomato;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    public interface OnMemoClickListener {
        void onMemoClick(int position);
    }

    private final List<Memo> memoList;
    private final OnMemoClickListener clickListener;

    public MemoAdapter(List<Memo> memoList, OnMemoClickListener clickListener) {
        this.memoList = memoList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_memo, parent, false);
        return new MemoViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        holder.bind(memo);
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    static class MemoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTextView;
        private final TextView contentSnippetTextView;
        private final OnMemoClickListener clickListener;

        public MemoViewHolder(@NonNull View itemView, OnMemoClickListener listener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.memo_title_text_view);
            contentSnippetTextView = itemView.findViewById(R.id.memo_content_snippet_text_view);
            this.clickListener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(Memo memo) {
            titleTextView.setText(memo.getTitle());
            contentSnippetTextView.setText(memo.getContent());
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onMemoClick(getAdapterPosition());
            }
        }
    }
}
