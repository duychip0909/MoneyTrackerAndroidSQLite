package com.example.moneytrackerandroidsqlite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.models.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Context context;
    private List<Transaction> transactions;
    private Transaction transaction;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        transaction = transactions.get(position);
        holder.txAmount.setText(String.valueOf(transaction.getFormated()));
        holder.txDesc.setText(transaction.getNotes());
        holder.txCate.setText(transaction.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txDesc, txCate, txAmount;
        CardView cardTx;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txDesc = itemView.findViewById(R.id.tvTransactionDescription);
            txCate = itemView.findViewById(R.id.tvTransactionCategory);
            txAmount = itemView.findViewById(R.id.tvTransactionAmount);
            cardTx = itemView.findViewById(R.id.tx_card);
        }
    }
}
