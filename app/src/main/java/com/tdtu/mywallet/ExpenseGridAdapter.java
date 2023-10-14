package com.tdtu.mywallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseGridAdapter extends RecyclerView.Adapter<ExpenseGridAdapter.ViewHolder> {
    private List<Expense> expenses;

    public ExpenseGridAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item_full,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.amountTextView.setText(expense.getAmount());
        holder.kindTextView.setText(expense.getKind());
        holder.descriptionTextView.setText(expense.getDescription());
        holder.dateTextView.setText(expense.getDate());
        holder.timeTextView.setText(expense.getTime());
    }

    @Override
    public int getItemCount() {
    return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTextView;
        public TextView kindTextView;
        public TextView descriptionTextView;
        public TextView dateTextView;
        public TextView timeTextView;

        public ViewHolder(View view) {
            super(view);
            amountTextView = view.findViewById(R.id.textViewAmount);
            kindTextView = view.findViewById(R.id.textViewKind);
            descriptionTextView = view.findViewById(R.id.textViewDescription);
            dateTextView = view.findViewById(R.id.textViewDate);
            timeTextView = view.findViewById(R.id.textViewTime);
        }
    }
}
