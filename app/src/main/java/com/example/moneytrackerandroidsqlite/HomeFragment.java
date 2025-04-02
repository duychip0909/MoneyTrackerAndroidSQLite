package com.example.moneytrackerandroidsqlite;

import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.adapters.TransactionAdapter;

public class HomeFragment extends Fragment {
    private TextView tvGreeting;
    private TextView tvDate;
    private TextView tvBalanceAmount;
    private TextView tvIncomeAmount;
    private TextView tvExpensesAmount;
    private RecyclerView rvRecentTransactions;
    private TransactionAdapter transactionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDate = view.findViewById(R.id.tvDate);
        tvBalanceAmount = view.findViewById(R.id.tvBalanceAmount);
        tvIncomeAmount = view.findViewById(R.id.tvIncomeAmount);
        tvExpensesAmount = view.findViewById(R.id.tvExpensesAmount);
        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions);

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));

        // Set up RecyclerView
        setupRecyclerView();

        // Load data
        loadData();

        return view;
    }

    private void setupRecyclerView() {
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        rvRecentTransactions.setAdapter(transactionAdapter);
    }
}
