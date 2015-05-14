//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper sInstance = null;

    private static final String DATABASE_NAME = "chickens.db";
    private static int DATABASE_VERSION = 5; // Increase this value to trigger an onUpgrade function call.

    private static final String PREFERENCES_CREATE =
            "CREATE Table Preferences (" +
                    "_id integer primary key," +
                    "LastFolder text not null," +
                    "LastTemplate text null," +
                    "Email text null," +
                    "DefaultObservationsAmount integer not null," +
                    "MaxObservationsAmount integer not null," +
                    "ShowRenameDialog integer not null," +
                    "UseNamePrefix integer not null);";

    public static DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context);
        }

        return sInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PREFERENCES_CREATE);
        insertInitialPreferences(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { // Upgrade db from version 1 to version 2.
            db.execSQL("ALTER TABLE Preferences ADD Column Email");

            // Insert initially empty email address.
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 1);
            contentValues.put("Email", "");
            db.update("Preferences", contentValues, null, null);
        }

        if (oldVersion < 3) { // Upgrade db from version 2 to version 3.
            db.execSQL("ALTER TABLE Preferences ADD Column DefaultObservationsAmount");
            db.execSQL("ALTER TABLE Preferences ADD Column MaxObservationsAmount");

            // Insert default values for new columns.
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 1);
            contentValues.put("DefaultObservationsAmount", 1);
            contentValues.put("MaxObservationsAmount", 10);
            db.update("Preferences", contentValues, null, null);
        }

        if (oldVersion < 4) { // Upgrade db from version 3 to version 4.
            db.execSQL("ALTER TABLE Preferences ADD Column ShowRenameDialog");

            // Insert default value.
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 1);
            contentValues.put("ShowRenameDialog", 1);
            db.update("Preferences", contentValues, null, null);
        }

        if (oldVersion < 5) { // Upgrade db from version 4 to version 5.
            db.execSQL("ALTER TABLE Preferences ADD Column UseNamePrefix");

            // Insert default value.
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 1);
            contentValues.put("UseNamePrefix", 1);
            db.update("Preferences", contentValues, null, null);
        }
    }

    private void insertInitialPreferences(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 1);
        contentValues.put("LastFolder", "Default");
        contentValues.put("Email", "");
        contentValues.put("DefaultObservationsAmount", 1);
        contentValues.put("MaxObservationsAmount", 10);
        contentValues.put("ShowRenameDialog", 1);
        contentValues.put("UseNamePrefix", 1);

        long result = db.insert("Preferences", null, contentValues);
        if (result == -1) {
            Log.e("Behave", "Failed to insert initial standings!");
        }
    }

    public String getFolder() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"LastFolder"}, null, null, null, null, null);
        String folder = "Default";
        if (cursor.moveToFirst()) {
            folder = cursor.getString(0);
        }

        cursor.close();
        return folder;
    }

    public String getTemplate() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"LastTemplate"}, null, null, null, null, null);
        String template = null;
        if (cursor.moveToFirst()) {
            template = cursor.getString(0);
        }

        cursor.close();
        return template;
    }

    public String getEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"Email"}, null, null, null, null, null);
        String email = null;
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }

        cursor.close();
        return email == null ? "" : email;
    }

    public int getDefaultObservationsAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"DefaultObservationsAmount"}, null, null, null, null, null);
        int amount = 1;
        if (cursor.moveToFirst()) {
            amount = cursor.getInt(0);
        }

        cursor.close();
        return amount;
    }

    public int getMaxObservationsAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"MaxObservationsAmount"}, null, null, null, null, null);
        int amount = 10;
        if (cursor.moveToFirst()) {
            amount = cursor.getInt(0);
        }

        cursor.close();
        return amount;
    }

    public boolean getShowRenameDialog() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"ShowRenameDialog"}, null, null, null, null, null);
        boolean show = true;
        if (cursor.moveToFirst()) {
            show = cursor.getInt(0) != 0;
        }

        cursor.close();
        return show;
    }

    public boolean getNamePrefix() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"UseNamePrefix"}, null, null, null, null, null);
        boolean prefix = true;
        if (cursor.moveToFirst()) {
            prefix = cursor.getInt(0) != 0;
        }

        cursor.close();
        return prefix;
    }

    public void setFolderTemplate(String folder, String template) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("LastFolder", folder);
        contentValues.put("LastTemplate", template);

        db.update("Preferences", contentValues, null, null);
    }

    public void setFolder(String folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("LastFolder", folder);

        db.update("Preferences", contentValues, null, null);
    }

    public void setTemplate(String template) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("LastTemplate", template);

        db.update("Preferences", contentValues, null, null);
    }

    public void setEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("Email", email);

        db.update("Preferences", contentValues, null, null);
    }

    public void setDefaultObservationsAmount(int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("DefaultObservationsAmount", amount);

        db.update("Preferences", contentValues, null, null);
    }

    public void setMaxObservationsAmount(int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("MaxObservationsAmount", amount);

        db.update("Preferences", contentValues, null, null);
    }

    public void setShowRenameDialog(boolean show) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("ShowRenameDialog", show ? 1 : 0);

        db.update("Preferences", contentValues, null, null);
    }

    public void setNamePrefix(boolean prefix) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", 1);
        contentValues.put("UseNamePrefix", prefix ? 1 : 0);

        db.update("Preferences", contentValues, null, null);
    }

    /**
     * Deletes the folder entry in the database if it is the current folder selected.
     * @param folder the folder name to remove
     */
    public void removeFolder(String folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"LastFolder"}, null, null, null, null, null);

        if (cursor.moveToNext()) {
            String current = cursor.getString(0);
            if (folder.equals(current)) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("_id", 1);
                contentValues.put("LastFolder", "Default");

                db.update("Preferences", contentValues, null, null);
            }
        }

        cursor.close();
    }

    /**
     * Deletes the template entry in the database if it is the current template selected.
     * @param template the template name to remove
     */
    public void removeTemplate(String template) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("Preferences", new String[] {"LastTemplate"}, null, null, null, null, null);

        if (cursor.moveToNext()) {
            String current = cursor.getString(0);
            if (template.equals(current)) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("_id", 1);
                contentValues.put("LastTemplate", (String) null);

                db.update("Preferences", contentValues, null, null);
            }
        }

        cursor.close();
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS Preferences");

        onCreate(db);
    }
}