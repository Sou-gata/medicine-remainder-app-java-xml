package com.sougata.meditrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    public static final String DB_NAME = "medicine_remainder";
    public static final int DB_VERSION = 1;

    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE medicines(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + //0
                "name TEXT, " + // 1
                "icon INTEGER DEFAULT 0," + // 2
                "repeat INTEGER DEFAULT 0," + // 3
                "creation_date INTEGER DEFAULT 1," + // 4
                "end_date INTEGER DEFAULT 0," + // 5
                "repeat_days TEXT DEFAULT \"0 0 0 0 0 0 0\"" + // 6
                ")");

        db.execSQL("CREATE TABLE times(" +
                "id INTEGER PRIMARY KEY," +
                "medicine_id INTEGER," +
                "time INTEGER," +
                "alarm_id TEXT DEFAULT '0'," +
                "FOREIGN KEY (medicine_id) REFERENCES medicines(id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS medicines");
        db.execSQL("DROP TABLE IF EXISTS times");
    }

    public void insertData(String name, int icon, int repeat, ArrayList<AddMedicineTimeContent> contents, long creationDate, long endDate, String repeatDays, String[] alarmIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("icon", icon);
        contentValues.put("repeat", repeat);
        contentValues.put("creation_date", creationDate);
        contentValues.put("end_date", endDate);
        contentValues.put("repeat_days", repeatDays);
        long newRowId = db.insert("medicines", null, contentValues);
        for (int i = 0; i < contents.size(); i++) {
            ContentValues c = new ContentValues();
            c.put("medicine_id", newRowId);
            c.put("time", contents.get(i).time);
            c.put("alarm_id", alarmIds[i]);
            db.insert("times", null, c);
        }
    }

    public void editMedicine(long id, String name, int icon, int repeat, ArrayList<AddMedicineTimeContent> contents, long creationDate, long endDate, String repeatDays, String[] alarmIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("icon", icon);
        contentValues.put("repeat", repeat);
        contentValues.put("creation_date", creationDate);
        contentValues.put("end_date", endDate);
        contentValues.put("repeat_days", repeatDays);
        long newRowId = db.update("medicines", contentValues, "id = ?", new String[]{String.valueOf(id)});
        db.delete("times", "medicine_id = ?", new String[]{String.valueOf(id)});
        for (int i = 0; i < contents.size(); i++) {
            ContentValues c = new ContentValues();
            c.put("medicine_id", newRowId);
            c.put("time", contents.get(i).time);
            c.put("alarm_id", alarmIds[i]);
            db.insert("times", null, c);
        }
    }

    public Cursor getAllMedicines() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM medicines", null);
    }

    public Cursor getTimesOfMedicine(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "medicine_id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.query("times", null, selection, selectionArgs, null, null, null);
    }


    public Cursor getSpecificMedicine(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.query("medicines", null, selection, selectionArgs, null, null, null);
    }

    public void deleteMedicine(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete("medicines", selection, selectionArgs);
        String selection2 = "medicine_id = ?";
        db.delete("times", selection2, selectionArgs);
    }

    public void deleteSingleAlarm(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete("times", selection, selectionArgs);
    }
}
