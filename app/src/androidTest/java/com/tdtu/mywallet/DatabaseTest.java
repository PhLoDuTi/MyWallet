package com.tdtu.mywallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    @Test
    public void testInsertDummyData() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ExpensesDBHelper dbHelper = new ExpensesDBHelper(appContext);

        // Get a writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Insert dummy data directly in the test
            ContentValues values1 = new ContentValues();
            values1.put("amount", "50000");
            values1.put("kind", "Food");
            values1.put("description", "Lunch");
            values1.put("date", "2023-01-15");
            values1.put("time", "12:30");
            long newRowId = db.insert("expenses", null, values1);

            // Query the database to check if the data was inserted successfully
            Cursor cursor = db.query("expenses",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            // Assert that data was inserted successfully
            assertTrue("No data was inserted into the database", cursor.moveToFirst());

            // Check the inserted data
            String amount = cursor.getString(cursor.getColumnIndex("amount"));
            String kind = cursor.getString(cursor.getColumnIndex("kind"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            assertEquals("50000", amount);
            assertEquals("Food", kind);
            assertEquals("Lunch", description);
            assertEquals("2023-01-15", date);
            assertEquals("12:30", time);

            cursor.close();
        } finally {
            // Close the database to release resources
            db.close();
        }
    }
}
