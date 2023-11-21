package com.tdtu.mywallet.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.tdtu.mywallet.main_menu.ExpensesDBHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

public class DataBackupManager {

    private static final String CSV_FILE_NAME = "expenses.csv";

    public static void exportData(AppCompatActivity activity) {
        // Start folder picker intent
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, generateExportFileName());

        activity.startActivityForResult(intent, 3);
    }

    private static String generateExportFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
        String dateTime = dateFormat.format(new Date());
        return "expense-" + dateTime + ".csv";
    }

    public static void exportDataToSelectedFile(Context context, Uri fileUri) {
        if (fileUri != null && isUriWritable(context, fileUri)) {
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri);

                outputStream = context.getContentResolver().openOutputStream(fileUri);

                // Get data from SQLite database (replace ExpenseDBHelper with your actual helper class)
                ExpensesDBHelper dbHelper = new ExpensesDBHelper(context);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM expenses", null);

                // Write data
                while (cursor.moveToNext()) {
                    //HACK Skip the "_id" attribute on the database
                    for (int i = 1; i < cursor.getColumnCount(); i++) {
                        outputStream.write((cursor.getString(i) + ",").getBytes());
                    }
                    outputStream.write("\n".getBytes());
                }

                cursor.close();
                db.close();
                outputStream.close();

                Toast.makeText(context, "Data exported to " + fileUri.getPath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Selected file not writable", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isUriWritable(Context context, Uri uri) {
        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
        return true;
    }


    public static void importData(AppCompatActivity activity) {
        // Start file picker intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");  // Set the MIME type to */* to allow all file types

        activity.startActivityForResult(intent, 1);
    }

    public static void handleImportResult(Context context, Uri uri) {
        try {
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);

            if (documentFile != null && documentFile.isFile() && documentFile.getName() != null) {
                String fileName = documentFile.getName();

                // Check if the file has a ".csv" extension
                if (fileName.toLowerCase().endsWith(".csv")) {
                    ParcelFileDescriptor parcelFileDescriptor =
                            context.getContentResolver().openFileDescriptor(uri, "r");

                    if (parcelFileDescriptor != null) {
                        FileInputStream fileInputStream =
                                new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

                        // Read the CSV file line by line
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] values = line.split(",");

                            ExpensesDBHelper dbHelper = new ExpensesDBHelper(context);
                            SQLiteDatabase db = dbHelper.getReadableDatabase();
                            long newRowId = dbHelper.importExpense(values[0], values[1], values[2], values[3], values[4]);

                            if (newRowId == -1) {
                                // Handle insertion failure
                                Toast.makeText(context, "Error importing data", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        reader.close();
                        fileInputStream.close();

                        // Show confirmation dialog before overwriting data
                        showConfirmationDialog(context);

                        //This is likely the ugliest error handling I have ever seen
                        //PLDT 21-11-2023 10:18
                        Toast.makeText(context, "Data imported successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please select a valid CSV file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Invalid file selection", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show();
        }
    }

    private static void showConfirmationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage("Importing data will overwrite existing data. Do you want to proceed?")

                // User clicked "Yes", proceed with the import
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Data imported successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                // User clicked "No", do nothing
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void wipeExpenses(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to wipe all expenses?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Yes," proceed with wiping expenses
                        try {
                            ExpensesDBHelper dbHelper = new ExpensesDBHelper(context);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            // Delete all rows from the expenses table
                            db.delete("expenses", null, null);

                            db.close();

                            Toast.makeText(context, "Expenses wiped", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Wipe failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No," do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
