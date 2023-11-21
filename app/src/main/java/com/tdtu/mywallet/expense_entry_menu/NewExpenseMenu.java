package com.tdtu.mywallet.expense_entry_menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.tdtu.mywallet.R;
import com.tdtu.mywallet.main_menu.DateTimeUtils;
import com.tdtu.mywallet.main_menu.Expense;
import com.tdtu.mywallet.main_menu.ExpensesDBHelper;

public class NewExpenseMenu extends AppCompatActivity {

    EditText editTextAmount, editTextKind, editTextDescription;

    TextView textViewDate, textViewTime, textViewTitle;

    Button buttonSave;

    private int year, month, day, hour, minute;

    private String isoFormattedDate;
    private String isoFormattedTime;

    private boolean isEdit;
    private boolean isDateEdited = false;
    private boolean isTimeEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense_menu);

        //TODO Port to View Binding
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextKind = findViewById(R.id.editTextKind);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        buttonSave = findViewById(R.id.buttonSave);
        //textViewTitle = findViewById(R.id.textViewTitle);
        buttonSave.setEnabled(false);

        //Special intent handling for when an expense entry is edited

        Intent intent = getIntent();
        Expense editExpense = intent.getParcelableExtra("EDIT_EXPENSE");
        isEdit = (editExpense != null);

        if (editExpense != null) {
            // Populate the UI fields with the data from editExpense
            editTextAmount.setText(editExpense.getAmount());
            editTextKind.setText(editExpense.getKind());
            editTextDescription.setText(editExpense.getDescription());
            textViewDate.setText(editExpense.getDate());
            textViewTime.setText(editExpense.getTime());
        }

        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("New Expense");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //Adjust UI depending if user is editing or making new expense
        if(isEdit){
            actionBar.setTitle("Edit Expense");
            //textViewTitle.setText("Edit Expense");
        }else {
            actionBar.setTitle("New Expense");
            //textViewTitle.setText("Add New Expense");
        }

        // Common TextWatcher
        TextWatcher commonTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        editTextAmount.addTextChangedListener(commonTextWatcher);
        editTextKind.addTextChangedListener(commonTextWatcher);
        editTextDescription.addTextChangedListener(commonTextWatcher);
        textViewDate.addTextChangedListener(commonTextWatcher);
        textViewTime.addTextChangedListener(commonTextWatcher);


        //ClickListeners for Date, Time and Save Expense
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input and save it to the database
                String amount = editTextAmount.getText().toString();
                String kind = editTextKind.getText().toString();
                String description = editTextDescription.getText().toString();

                String date = textViewDate.getText().toString().trim();
                String time = textViewTime.getText().toString().trim();

                // Check for empty fields and assign default values if true
                if (description.isEmpty()) {
                    description = "(None)";
                }
                date = handleDate(date,isoFormattedDate);
                time = handleTime(time,isoFormattedTime);


                // Open the database
                ExpensesDBHelper dbHelper = new ExpensesDBHelper(NewExpenseMenu.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();


                // Check if we are editing an existing expense or creating a new one
                if (isEdit) {
                    //HACK prevent null on date and time when it hasn't been edited.
                    String currentDate = textViewDate.getText().toString();
                    String currentTime = textViewTime.getText().toString();

                    // HACK Handle the case where date and time are not edited
                    if (isDateEdited) {
                        currentDate = handleDate(currentDate,isoFormattedDate);
                    }
                    if (isTimeEdited) {
                        currentTime = handleTime(currentTime,isoFormattedTime);
                    }


                    ContentValues values = new ContentValues();
                    values.put("amount", amount);
                    values.put("kind", kind);
                    values.put("description", description);
                    values.put("date", currentDate);
                    values.put("time", currentTime);


                    String[] whereArgs = {String.valueOf(editExpense.getId())};
                    int rowsUpdated = db.update("expenses", values, "_id=?", whereArgs);

                    if (rowsUpdated != -1) {
                        Toast.makeText(NewExpenseMenu.this,
                                "Expense edited saved successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewExpenseMenu.this,
                                "Failed to save expense edits",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ContentValues values = new ContentValues();
                    values.put("amount", amount);
                    values.put("kind", kind);
                    values.put("description", description);
                    values.put("date", date);
                    values.put("time", time);

                    long newRowId = db.insert("expenses", null, values);

                    if (newRowId != -1) {
                        Toast.makeText(NewExpenseMenu.this,
                                "Expense saved successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewExpenseMenu.this,
                                "Failed to save expense",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                // Close the database
                db.close();
            }

            private String handleTime(String time, String isoFormattedTime) {
                // Handle empty or invalid time
                if (time.isEmpty()) {
                    // Assign the current time as the default value
                    return DateTimeUtils.getCurrentTime();
                } else {
                    return isoFormattedTime;
                }
            }

            // Handle empty or invalid date
            private String handleDate(String date, String isoFormattedDate) {
                if (date.isEmpty()) {
                    // Assign the current date as the default value
                    return DateTimeUtils.getCurrentDate();
                } else {
                    return isoFormattedDate;
                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Format the date to ISO 8601
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        String isoDate = DateTimeUtils.formatToISO8601Date(selectedDate);
                        isoFormattedDate = isoDate;

                        // Display the formatted date in the TextView
                        textViewDate.setText(DateTimeUtils.formatToUserLocaleDate(isoDate));
                        isDateEdited = true;
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Format the time to ISO 8601
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        String isoTime = DateTimeUtils.formatToISO8601Time(selectedTime);
                        isoFormattedTime = isoTime;

                        // Display the formatted time in the TextView
                        textViewTime.setText(DateTimeUtils.formatToUserLocaleTime(isoTime));
                        isTimeEdited = true;
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    //-------------------------
    // Handle back button press
    //-------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSaveButtonStatus() {
        String amount = editTextAmount.getText().toString().trim();
        String kind = editTextKind.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = textViewDate.getText().toString().trim();
        String time = textViewTime.getText().toString().trim();

        // Check if amount and kind are non-empty, and optionally check for date and time
        // If description is non-empty, or date/time is non-empty, consider them as filled

        boolean allFieldsFilled = !amount.isEmpty() && !kind.isEmpty();

        if (!description.isEmpty() || !date.isEmpty() || !time.isEmpty()) {
            allFieldsFilled = true;
        }
        buttonSave.setEnabled(allFieldsFilled);
    }


}
