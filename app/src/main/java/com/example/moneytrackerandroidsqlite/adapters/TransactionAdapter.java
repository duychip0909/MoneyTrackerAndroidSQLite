package com.example.moneytrackerandroidsqlite.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.TransactionDetailActivity;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.models.TransactionListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_TRANSACTION = 1;

    private Context context;
    private List<TransactionListItem> items;
    private Map<String, Double> dateTotals;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.items = new ArrayList<>();
        this.dateTotals = new TreeMap<>();
        setTransactions(transactions);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionListItem item = items.get(position);

        if (holder instanceof DateViewHolder) {
            DateViewHolder dateHolder = (DateViewHolder) holder;
            String dateText = item.getDateText();

            // Set date text
            dateHolder.tvDate.setText(dateText);

            // Set the total amount for this date
            double total = dateTotals.get(dateText);
            String formattedTotal = String.format("%s%,.0fđ", (total >= 0 ? "+" : ""), total);
            dateHolder.tvTotal.setText(formattedTotal);

            // Set text color based on whether the total is positive or negative
            if (total >= 0) {
                dateHolder.tvTotal.setTextColor(context.getColor(R.color.green));
            } else {
                dateHolder.tvTotal.setTextColor(context.getColor(R.color.red));
            }
        } else if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder txHolder = (TransactionViewHolder) holder;
            Transaction transaction = item.getTransaction();

            txHolder.txAmount.setText(transaction.getFormated());
            if (transaction.getType() == Transaction.Type.EXPENSE) {
                txHolder.txAmount.setTextColor(context.getColor(R.color.red));
            } else {
                txHolder.txAmount.setTextColor(context.getColor(R.color.green));
            }
            txHolder.txDesc.setText(transaction.getNotes());
            txHolder.txCate.setText(transaction.getCategoryName());
            txHolder.cardTx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TransactionDetailActivity.class);
                    intent.putExtra("TRANSACTION_ID", transaction.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        TransactionListItem item = items.get(position);
        return (item.getType() == TransactionListItem.ItemType.DATE_HEADER) ? VIEW_TYPE_DATE : VIEW_TYPE_TRANSACTION;
    }

    public void setTransactions(List<Transaction> transactions) {
        // Group transactions by date and calculate totals
        TreeMap<String, List<Transaction>> groupedTransactions = new TreeMap<>((date1, date2) -> {
            // Sort in reverse order (newest first)
            return date2.compareTo(date1);
        });

        dateTotals.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

        for (Transaction transaction : transactions) {
            Date date = new Date(transaction.getDate());
            String dateStr = dateFormat.format(date);

            if (!groupedTransactions.containsKey(dateStr)) {
                groupedTransactions.put(dateStr, new ArrayList<>());
                dateTotals.put(dateStr, 0.0);
            }

            groupedTransactions.get(dateStr).add(transaction);

            // Calculate total: add for INCOME, subtract for EXPENSE
            double currentTotal = dateTotals.get(dateStr);
            if (transaction.getType() == Transaction.Type.INCOME) {
                currentTotal += transaction.getAmount();
            } else {
                currentTotal -= transaction.getAmount();
            }
            dateTotals.put(dateStr, currentTotal);
        }

        // Clear current items and add date headers and transactions
        items.clear();

        for (Map.Entry<String, List<Transaction>> entry : groupedTransactions.entrySet()) {
            // Add date header
            items.add(new TransactionListItem(entry.getKey()));

            // Add transactions for this date
            for (Transaction transaction : entry.getValue()) {
                items.add(new TransactionListItem(transaction));
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<TransactionListItem> getItems() {
        return items;
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvTotal;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTotal = itemView.findViewById(R.id.tvDateTotal);
        }
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView txDesc, txCate, txAmount;
        CardView cardTx;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            txDesc = itemView.findViewById(R.id.tvTransactionDescription);
            txCate = itemView.findViewById(R.id.tvTransactionCategory);
            txAmount = itemView.findViewById(R.id.tvTransactionAmount);
            cardTx = itemView.findViewById(R.id.tx_card);
        }
    }
}