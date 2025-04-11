package com.example.moneytrackerandroidsqlite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.models.Budget;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    Context context;
    List<Budget> budgets;
    public interface OnItemClickListener {
        void onItemClick(Long budgetId);
    }
    private OnItemClickListener listener;
    public BudgetAdapter(Context context, List<Budget> budgets, OnItemClickListener listener) {
        this.context = context;
        this.budgets = budgets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.budget_item, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);
        holder.categoryBudget.setText(budget.getCategoryName());
        holder.budgetAmount.setText(String.format("%,.0fđ", budget.getSpentAmount()));
        holder.budgetAmount.setText(String.format("%,.0fđ", budget.getRemainingAmount()));
        int progress = (int) budget.getProgressPercentage();
        holder.progressBar.setProgress(progress);
        holder.cardBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(budget.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        CardView cardBudget;
        TextView budgetAmount, remainingAmount, categoryBudget;
        ProgressBar progressBar;
        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBudget = itemView.findViewById(R.id.cardBudget);
            budgetAmount = itemView.findViewById(R.id.tv_budget_amount);
            remainingAmount = itemView.findViewById(R.id.tv_remaining_amount);
            categoryBudget = itemView.findViewById(R.id.tv_category);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
