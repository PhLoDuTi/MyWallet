package com.tdtu.mywallet;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.List;

public class StatisticsMenu extends AppCompatActivity {

    private RecyclerView expensesRecyclerView;
    private ExpenseGridAdapter expenseAdapter;
    private List<Expense> expenses;

    private SQLiteDatabase db;
    private ExpensesDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_menu);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Statistics");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        expensesRecyclerView = findViewById(R.id.recyclerViewExpenses);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ---------------------------------------
        // Retrieve expense data from the database
        // ---------------------------------------

        dbHelper = new ExpensesDBHelper(StatisticsMenu.this);
        db = dbHelper.getWritableDatabase();
        expenses = dbHelper.getExpensesGroupedByKind();

        if (expenses != null) {
            expenseAdapter = new ExpenseGridAdapter(expenses);
            expensesRecyclerView.setAdapter(expenseAdapter);
        } else {
            Toast.makeText(StatisticsMenu.this,
                    "No expenses!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}