package com.example.moneytrackerandroidsqlite.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DateGroupAdapter extends RecyclerView.Adapter<DateGroupAdapter.DateGroupViewHolder> {
    private Context context;
    private List<String> dateList;
    private Map<String, List<Transaction>> groupedTxs;
    private Map<String, Double> dateTotals;

    public DateGroupAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.dateList = new ArrayList<>();
        this.groupedTxs = new LinkedHashMap<>();
        this.dateTotals = new LinkedHashMap<>();
        setTransactions(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        TreeMap<String, List<Transaction>> sortedTxs = new TreeMap<>((date1, date2) -> {
           return date2.compareTo(date1);
        });
        Map<String, Double> totals = new LinkedHashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        for (Transaction transaction : transactions) {
            Date date = new Date(transaction.getDate());
            String dateStr = dateFormat.format(date);
            if (!sortedTxs.containsKey(dateStr)) {
                sortedTxs.put(dateStr, new ArrayList<>());
                totals.put(dateStr, 0.0);
            }
            sortedTxs.get(dateStr).add(transaction);
            double currentTotal = totals.get(dateStr);
            if (transaction.getType() == Transaction.Type.INCOME) {
                currentTotal += transaction.getAmount();
            } else {
                currentTotal -= transaction.getAmount();
            }
            totals.put(dateStr, currentTotal);
        }
        dateList.clear();
        groupedTxs.clear();
        dateTotals.clear();
        dateList.addAll(sortedTxs.keySet());
        groupedTxs.putAll(sortedTxs);
        dateTotals.putAll(totals);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateGroupAdapter.DateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date_header, parent, false);
        return new DateGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateGroupAdapter.DateGroupViewHolder holder, int position) {
        String dateText = dateList.get(position);
        List<Transaction> transactions = groupedTxs.get(dateText);
        double total = dateTotals.get(dateText);
        holder.tvDate.setText(dateText);
        String formattedTotal = String.format("%s%,.0fđ", (total >= 0 ? "+" : ""), total);
        holder.tvTotal.setText(formattedTotal);
        if (total >= 0) {
            holder.tvTotal.setTextColor(context.getColor(R.color.green));
        } else {
            holder.tvTotal.setTextColor(context.getColor(R.color.red));
        }
        TransactionAdapter transactionAdapter = new TransactionAdapter(context, transactions);
        holder.rvTxs.setLayoutManager(new LinearLayoutManager(context));
        holder.rvTxs.setAdapter(transactionAdapter);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class DateGroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTotal;
        RecyclerView rvTxs;

        public DateGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTotal = itemView.findViewById(R.id.tvDateTotal);
            rvTxs = itemView.findViewById(R.id.rvDateTransactions);
        }
    }
}
