package com.example.moneytrackerandroidsqlite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactions;

    public interface OnItemClickListener {
        void onItemClick(Long transactionId);
    }
    private OnItemClickListener listener;

    public TransactionAdapter(Context context, List<Transaction> transactions, OnItemClickListener listener) {
        this.context = context;
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.txAmount.setText(transaction.getFormated());
        if (transaction.getType() == Transaction.Type.EXPENSE) {
            holder.txAmount.setTextColor(context.getColor(R.color.red));
        } else {
            holder.txAmount.setTextColor(context.getColor(R.color.green));
        }

        holder.txDesc.setText(transaction.getNotes());
        holder.txCate.setText(transaction.getCategoryName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(transaction.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView txDesc, txCate, txAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            txDesc = itemView.findViewById(R.id.tvTransactionDescription);
            txCate = itemView.findViewById(R.id.tvTransactionCategory);
            txAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}