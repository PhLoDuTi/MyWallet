package com.tdtu.mywallet;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;


public class StatisticsMenu extends AppCompatActivity {

    private RecyclerView expensesRecyclerView;
    private ExpenseGridAdapter expenseAdapter;
    private List<Expense> expenses;
    private List<ExpenseDaily> allExpenses;
    private Map<String, Double> totalByDateForLineChart;
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

        totalByDateForLineChart = new HashMap<>();

        // ---------------------------------------
        // Retrieve expense data from the database
        // ---------------------------------------

        dbHelper = new ExpensesDBHelper(StatisticsMenu.this);
        db = dbHelper.getWritableDatabase();
        expenses = dbHelper.getExpensesGroupedByKind();
        allExpenses = dbHelper.getExpensesMadeToday();

        // ------------------------------------------
        // Get all the expense's kinds and count them
        // ------------------------------------------

        List<ExpensePercentage> expensePercentages = new ArrayList<>();
        int totalExpenses = allExpenses.size();

        Map<String, Integer> kindCount = new HashMap<>();
        Map<String, Double> kindTotalAmount = new HashMap<>();

        for (ExpenseDaily expense : allExpenses) {
            String kind = expense.getKind();
            kindCount.put(kind, kindCount.getOrDefault(kind, 0) + 1);
            kindTotalAmount.put(kind, kindTotalAmount.getOrDefault(kind, 0.0) + Double.parseDouble(expense.getAmount()));
        }

        // -----------------------------------
        // Calculate percentages for each kind
        // -----------------------------------

        for (Map.Entry<String, Integer> entry : kindCount.entrySet()) {
            String kind = entry.getKey();
            int count = entry.getValue();
            double totalAmount = kindTotalAmount.get(kind);

            double percentage = (count / (double) totalExpenses) * 100;

            // Add to the list of ExpensePercentages
            expensePercentages.add(new ExpensePercentage(kind, percentage, totalAmount));
        }

        // ------------------------------------
        // Sort percentages in descending order
        // ------------------------------------

        expensePercentages.sort((p1, p2)
                -> Double.compare(p2.getPercentage(), p1.getPercentage()));


        RecyclerView recyclerView = findViewById(R.id.recyclerViewExpensePercentages);

        ExpensePercentageAdapter adapter = new ExpensePercentageAdapter(expensePercentages);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ----------------------------
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_detailed) {
            startActivity(new Intent(this, StatisticsDetails.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ExpensePercentage {

        private String expenseKind;
        private double percentage;
        private double totalAmount;

        public ExpensePercentage(String expenseType, double percentage, double totalAmount) {
            this.expenseKind = expenseType;
            this.percentage = percentage;
            this.totalAmount = totalAmount;
        }

        public String getExpenseType() {
            return expenseKind;
        }

        public double getPercentage() {
            return percentage;
        }

        public double getTotalAmount() {
            return totalAmount;
        }
    }


}