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
import com.example.moneytrackerandroidsqlite.models.Category;

import java.util.List;

public class CategoryAdapterEdit extends RecyclerView.Adapter<CategoryAdapterEdit.ViewHolder> {
    List<Category> categories;
    Context context;
    CategoryAdapterEdit.OnCategoryClickListener listener;
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    public CategoryAdapterEdit(Context context, List<Category> categories, CategoryAdapterEdit.OnCategoryClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public CategoryAdapterEdit.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapterEdit.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapterEdit.ViewHolder holder, int position) {
        final Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardCategory;
        TextView categoryName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCate);
            categoryName = itemView.findViewById(R.id.tvExpenseCate);
        }
    }
}
