package com.tdtu.mywallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    private ExpenseGridAdapterDaily expenseAdapterDaily;
    private SQLiteDatabase db;
    private ExpensesDBHelper dbHelper;
    private List<ExpenseDaily> expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //--------------------------------------
        //Initialize the database upon launching
        //--------------------------------------
        dbHelper = new ExpensesDBHelper(this);
        db = dbHelper.getWritableDatabase();

        RecyclerView expensesRecyclerViewDaily = findViewById(R.id.recyclerViewExpensesDaily);
        expensesRecyclerViewDaily.setLayoutManager(new LinearLayoutManager(this));

        //--------------
        //Query database
        //--------------

        dbHelper = new ExpensesDBHelper(MainMenu.this);
        db = dbHelper.getWritableDatabase();
        expenses = dbHelper.getExpensesMadeToday();
        updateRecyclerViewData();


        //--------------
        //Displaying data
        //--------------

        //-------------------------
        //Display data for TextViews
        //-------------------------

        setTodayDate();
        setMonthlyExpenses();
        setTodayExpenses();

        //-------------------------
        //Display data for GridView
        //-------------------------

        if (expenses != null) {
            expenseAdapterDaily = new ExpenseGridAdapterDaily(expenses);
            expensesRecyclerViewDaily.setAdapter(expenseAdapterDaily);
        } else {
            Toast.makeText(MainMenu.this,
                    "No expenses!",
                    Toast.LENGTH_SHORT).show();
        }
   }

    //----------------------------
    //Handling of database queries
    //----------------------------

    private void setTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        String todayDate = dateFormat.format(new Date());
        TextView dateTextView = findViewById(R.id.textViewToday);
        dateTextView.setText("Today is " + todayDate);
    }

    private void setMonthlyExpenses() {
        double totalExpenses = dbHelper.getTotalExpensesForMonth();
        TextView totalExpensesTextView = findViewById(R.id.textViewMonthlyExpense);
        totalExpensesTextView.setText("Total Expenses (Month): " + totalExpenses);

    }

    private void setTodayExpenses() {
        double todayExpenses = dbHelper.getTotalExpensesForToday();
        TextView todayExpensesTextView = findViewById(R.id.textViewTodayExpense);
        todayExpensesTextView.setText("Today's Expenses: " + todayExpenses);
    }



   //-------------------------
   //Handling of button clicks
   //-------------------------

   public void onClickStatistics(View view) {

       Intent intent = new Intent(MainMenu.this, StatisticsMenu.class);
       startActivity(intent);

   }

   public void onClickExpenseAdd(View view) {

       Intent intent = new Intent(MainMenu.this, NewExpenseMenu.class);
       startActivity(intent);

   }

   //-----------------------------------------------------------
   //Update RecyclerView when we go to or get back to this view.
   //-----------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        updateRecyclerViewData();
    }

    private void updateRecyclerViewData() {
        List<ExpenseDaily> updatedData = dbHelper.getExpensesMadeToday();

        // Update the adapter with the new data
        if (expenseAdapterDaily != null) {
            expenseAdapterDaily.updateData(updatedData);
        }
    }


   //----------------------------------------------------
   //Making sure the database is closed when the app does
   //----------------------------------------------------

   @Override
   protected void onDestroy() {
       super.onDestroy();
       if (db != null) {
           db.close();
       }
   }
}