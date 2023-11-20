package com.tdtu.mywallet;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainMenu extends AppCompatActivity {

    private ExpenseGridAdapterDaily expenseAdapterDaily;
    private ExpenseGroupedAdapter groupedAdapter;
    private SQLiteDatabase db;
    private ExpensesDBHelper dbHelper;
    private List<ExpenseDaily> expenses;

    private List<Expense> expenseNum;

    private Map<String, List<ExpenseDaily>> expensesByDate;

    private List<String> uniqueDates;

    private RecyclerView expensesRecyclerViewDaily;
    private RecyclerView expensesRecyclerViewGrouped;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //------------------------------
        //Enable persistent notification
        //------------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "expense_channel",
                    "Expense Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent serviceIntent = new Intent(this, QuickExpenseNotificationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }


        //--------------------------------------
        //Initialize the database upon launching
        //--------------------------------------
        dbHelper = new ExpensesDBHelper(this);
        db = dbHelper.getWritableDatabase();

        //Init expense RecyclerViews
        expensesRecyclerViewDaily = findViewById(R.id.recyclerViewExpensesDaily);
        expensesRecyclerViewDaily.setLayoutManager(new LinearLayoutManager(this));

        expensesRecyclerViewGrouped = findViewById(R.id.recyclerViewExpensesGrouped);
        expensesRecyclerViewGrouped.setLayoutManager(new LinearLayoutManager(this));

        //------------------------------------
        //Initialize spinner for expense views
        //--------------------------------------

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.expense_select_spinner);

        // Initialize the Spinner and set up its adapter and listener
        Spinner spinnerExpenseView = actionBar.getCustomView().findViewById(R.id.spinnerExpenseView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_view_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseView.setAdapter(adapter);

        spinnerExpenseView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    // Today's Expenses
                    case 0:
                        displayTodayExpenses();
                        break;
                    // Grouped Expenses
                    case 1:
                        displayGroupedExpenses();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        //--------------
        //Query database
        //--------------

        dbHelper = new ExpensesDBHelper(MainMenu.this);
        db = dbHelper.getWritableDatabase();
        expenses = dbHelper.getExpensesMadeToday();
        expensesByDate = dbHelper.getExpensesGroupedByDate();

        updateRecyclerViewData();

        //Setting up listeners
        //
        //It isn't an elegant solution, but it would work for now...
        //PLDT 16-10-2023 10:08

       RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(this,
               expensesRecyclerViewDaily,
               new RecyclerTouchListener.ClickListener() {
                   //Editing an expense
                   @Override
                   public void onClick(View view, int position) {
                       // Get the selected expense
                       expenseNum = dbHelper.retrieveExpenseDataFromDatabase();
                       Expense selectedExpense = expenseNum.get(position);

                       // Create an intent to start the ExpenseEntryActivity in edit mode
                       Intent editIntent = new Intent(MainMenu.this,
                               NewExpenseMenu.class);
                       editIntent.putExtra("EDIT_EXPENSE", selectedExpense);
                       startActivity(editIntent);
                   }

                   //Deleting an expense
                   @Override
                   public void onLongClick(View view, final int position) {
                       AlertDialog.Builder alertDialog = new AlertDialog.
                               Builder(MainMenu.this);
                       alertDialog.setTitle("Delete Entry");
                       alertDialog.setMessage("Are you sure you want to delete this entry?");

                       alertDialog.setPositiveButton("YES",
                               new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       expenseNum = dbHelper.retrieveExpenseDataFromDatabase();

                                       // Delete the entry from the list and notify the adapter
                                       Expense deletedExpense = expenseNum.remove(position);

                                       // Remove the entry from the database
                                       long expenseId = deletedExpense.getId();
                                       // Call the database deletion method
                                       dbHelper.deleteExpense(expenseId);

                                       dialog.dismiss();
                                       updateRecyclerViewData();
                                   }
                               });

                       alertDialog.setNegativeButton("NO",
                               new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                   }
                               });

                       alertDialog.show();
                   }

               });
       expensesRecyclerViewDaily.addOnItemTouchListener(recyclerTouchListener);

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

        if (expenses != null || expensesByDate != null) {
            //grouped
            uniqueDates = dbHelper.getUniqueDates();
            groupedAdapter = new ExpenseGroupedAdapter(expensesByDate, uniqueDates);
            expensesRecyclerViewGrouped.setAdapter(groupedAdapter);

            //daily
            expenseAdapterDaily = new ExpenseGridAdapterDaily(expenses);
            expensesRecyclerViewDaily.setAdapter(expenseAdapterDaily);
        } else {
            Toast.makeText(MainMenu.this,
                    "No expenses!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            // Open the Settings activity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (itemId == R.id.action_about) {
            // Handle the About option
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayTodayExpenses() {
        expenseAdapterDaily.updateData(dbHelper.getExpensesMadeToday());
        expensesRecyclerViewDaily.setVisibility(View.VISIBLE);
        expensesRecyclerViewGrouped.setVisibility(View.GONE);
    }

    private void displayGroupedExpenses() {
        groupedAdapter.updateData(dbHelper.getExpensesGroupedByDate());
        expensesRecyclerViewDaily.setVisibility(View.GONE);
        expensesRecyclerViewGrouped.setVisibility(View.VISIBLE);
    }

    //----------------------------
    //Handling of database queries
    //----------------------------

    private void setTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        String todayDate = dateFormat.format(new Date());
        TextView dateTextView = findViewById(R.id.textViewToday);
        dateTextView.setText(todayDate);
    }

    private void setMonthlyExpenses() {
        double totalExpenses = dbHelper.getTotalExpensesForMonth();
        TextView totalExpensesTextView = findViewById(R.id.textViewMonthlyExpense);
        totalExpensesTextView.setText("Total Expenses (Month): \n" + String.format("%.0f",totalExpenses));

    }

    private void setTodayExpenses() {
        double todayExpenses = dbHelper.getTotalExpensesForToday();
        TextView todayExpensesTextView = findViewById(R.id.textViewTodayExpense);
        todayExpensesTextView.setText("Today's Expenses: \n" + String.format("%.0f",todayExpenses));
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

   //----------------------------------
   //Handling editing expenses manually
   //----------------------------------


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
            //HACK When deleting an expense, changes should be reflected onto all fields.
            setTodayDate();
            setMonthlyExpenses();
            setTodayExpenses();
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