package com.tdtu.mywallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseGridAdapterDaily extends
        RecyclerView.Adapter<com.tdtu.mywallet.ExpenseGridAdapterDaily.ViewHolder> {

    private List<ExpenseDaily> expenses;


    public int getItemCount() {
        return expenses.size();
    }

    public ExpenseGridAdapterDaily(List<ExpenseDaily> expenses) {
        this.expenses = expenses;
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExpenseDaily expense = expenses.get(position);
        holder.amountTextView.setText(expense.getAmount());
        holder.kindTextView.setText(expense.getKind());
        holder.descriptionTextView.setText(expense.getDescription());
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
