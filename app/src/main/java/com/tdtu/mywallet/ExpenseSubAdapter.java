package com.tdtu.mywallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseSubAdapter extends
        RecyclerView.Adapter<ExpenseSubAdapter.ViewHolder> {

    private List<ExpenseDaily> expenses;
    private Context context;

    public ExpenseSubAdapter(Context context, List<ExpenseDaily> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_sub_item,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
}
