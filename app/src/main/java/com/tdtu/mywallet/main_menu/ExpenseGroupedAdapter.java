package com.tdtu.mywallet.main_menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.mywallet.R;

import java.util.List;
import java.util.Map;

public class ExpenseGroupedAdapter extends
        RecyclerView.Adapter<ExpenseGroupedAdapter.ViewHolder> {
    private Map<String, List<ExpenseDaily>> groupedExpenses;
    private List<String> dates;
    private Context context;

    public ExpenseGroupedAdapter(
                                 Map<String, List<ExpenseDaily>> groupedExpenses,
                                 List<String> dates) {
        this.groupedExpenses = groupedExpenses;
        this.dates = dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_grouped_item,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = dates.get(position);
        List<ExpenseDaily> expenses = groupedExpenses.get(date);

        holder.dateTextView.setText(date);

        // Create a sub-adapter to display expenses for each date
        ExpenseSubAdapter subAdapter = new ExpenseSubAdapter(context, expenses);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(subAdapter);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public void updateData(Map<String, List<ExpenseDaily>> newData) {
        groupedExpenses = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;

        // Using nested RecyclerView for displaying expenses
        public RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.textViewDate);

            // Connect to the nested RecyclerView
            recyclerView = view.findViewById(R.id.recyclerViewExpenses);
        }
    }
}
