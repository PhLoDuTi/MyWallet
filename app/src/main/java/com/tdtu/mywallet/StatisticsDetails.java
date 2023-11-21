package com.tdtu.mywallet;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class StatisticsDetails extends AppCompatActivity {
    private RecyclerView expensesRecyclerView;
    private ExpenseGridAdapter expenseAdapter;
    private List<Expense> expenses;
    private List<ExpenseDaily> allExpenses;
    private Map<String, Double> totalByDateForLineChart, totalByDateForPieChart;
    private Map<String, Double> kindTotalAmountAll;
    private LineChart lineChart;
    private PieChart pieChart;
    private SQLiteDatabase db;
    private ExpensesDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Statistics Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        expensesRecyclerView = findViewById(R.id.recyclerViewExpensesChange);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));





        // Create line chart
        totalByDateForLineChart = new HashMap<>();
        lineChart = findViewById(R.id.lineChart);
        lineChart.getDescription().setText("");

        //Create pie chart
        totalByDateForPieChart = new HashMap<>();
        pieChart = findViewById(R.id.pieChart);
        pieChart.getDescription().setText("");


        // ---------------------------------------
        // Retrieve expense data from the database
        // ---------------------------------------

        dbHelper = new ExpensesDBHelper(StatisticsDetails.this);
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
            kindTotalAmount.put(kind, kindTotalAmount.getOrDefault(kind, 0.0) +
                    Double.parseDouble(expense.getAmount()));
        }


        kindTotalAmountAll = kindTotalAmount;

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





        // ----------------------------
        // Get expenses grouped by date
        // ----------------------------

        Set<String> date = dbHelper.getExpensesGroupedByDate().keySet();

        Map<String, List<ExpenseDaily>> expenseByDate = dbHelper.getExpensesGroupedByDate();


        for(String d : date){
            totalByDateForLineChart.put(d, sumDate(expenseByDate.get(d)));

        }
        updateLineChart();
        updatePieChart();


        if (expenses != null) {
            expenseAdapter = new ExpenseGridAdapter(expenses);
            expensesRecyclerView.setAdapter(expenseAdapter);
        } else {
            Toast.makeText(StatisticsDetails.this,
                    "No expenses!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private double sumDate(List<ExpenseDaily> expenseDaily) {

        double total = 0;
        if (expenseDaily != null) {
            for (ExpenseDaily expense : expenseDaily) {
                total += Double.parseDouble(expense.getAmount());
            }
        }
        return total;
    }
    private void updatePieChart() {
        if (totalByDateForPieChart == null) {
            pieChart.setData(new PieData());
            pieChart.invalidate();
            return;
        }
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : kindTotalAmountAll.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));

        }


        //get color for pie chart
        // create object of Random class
        Random rnd = new Random();
        List<Integer> colors = new ArrayList<>();
        for(int i = 0; i < entries.size(); i++) {
            int rand_num = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));   ;
            colors.add(rand_num);
        }




        PieDataSet dataSet = new PieDataSet(entries, "Total Expense:");
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
    private void updateLineChart() {
        // Ensure totalByDateForLineChart is not null
        if (totalByDateForLineChart == null) {
            // Set an empty data set to make the LineChart disappear
            lineChart.setData(new LineData());
            lineChart.invalidate();
            return;
        }

        // Extract data from totalByDateForLineChart and create Entry objects
        Set<String> date = dbHelper.getExpensesGroupedByDate().keySet();
        List<Entry> entries = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Double> entry : totalByDateForLineChart.entrySet()) {
            entries.add(new Entry(index++, entry.getValue().floatValue()));
        }

        // Create a LineDataSet with the entries
        LineDataSet dataSet = new LineDataSet(entries, "Total Expenses");

        // Create a LineData object with the LineDataSet
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize X-axis and Y-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int position = (int) value;
                if (position >= 0 && position < date.size()) {
                    return (String) date.toArray()[position];
                } else {
                    return "";
                }
            }
        });

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularity(1f);

        // Refresh the chart
        lineChart.invalidate();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press
            onBackPressed();
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
