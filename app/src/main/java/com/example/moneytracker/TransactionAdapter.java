package com.example.moneytracker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnItemClickListener { void onItemClick(Transaction transaction); }
    private List<Transaction> transactionList;
    private OnItemClickListener listener;

    public TransactionAdapter(List<Transaction> list, OnItemClickListener listener) {
        this.transactionList = list;
        this.listener = listener;
    }

    @NonNull @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactionList.get(position);
        holder.tvCat.setText(t.getCategory());
        holder.tvDesc.setText(t.getDescription());
        holder.tvAmt.setText(("INCOME".equals(t.getType()) ? "+" : "-") + " $" + t.getAmount());
        holder.tvAmt.setTextColor(Color.parseColor("INCOME".equals(t.getType()) ? "#4CAF50" : "#F44336"));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(t));
    }

    @Override public int getItemCount() { return transactionList.size(); }
    public Transaction getTransactionAt(int pos) { return transactionList.get(pos); }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCat, tvDesc, tvAmt;
        public TransactionViewHolder(@NonNull View v) {
            super(v);
            tvCat = v.findViewById(R.id.tvCategory);
            tvDesc = v.findViewById(R.id.tvDescription);
            tvAmt = v.findViewById(R.id.tvAmount);
        }
    }
}