package com.tdtu.mywallet.main_menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.mywallet.R;

import java.util.List;

public class ExpenseGridAdapterDaily extends
        RecyclerView.Adapter<ExpenseGridAdapterDaily.ViewHolder> {
    private List<ExpenseDaily> expenses;

    public ExpenseGridAdapterDaily(List<ExpenseDaily> expenses) {
        this.expenses = expenses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExpenseDaily expense = expenses.get(position);
        holder.amountTextView.setText(expense.getAmount());
        holder.kindTextView.setText(expense.getKind());
        holder.descriptionTextView.setText(expense.getDescription());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTextView;
        public TextView kindTextView;
        public TextView descriptionTextView;

        public ViewHolder(View view) {
            super(view);
            amountTextView = view.findViewById(R.id.textViewAmount);
            kindTextView = view.findViewById(R.id.textViewKind);
            descriptionTextView = view.findViewById(R.id.textViewDescription);
        }
    }

    public void updateData(List<ExpenseDaily> newData) {
        expenses = newData;
        notifyDataSetChanged();
    }
}
