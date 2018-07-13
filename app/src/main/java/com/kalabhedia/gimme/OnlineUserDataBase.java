package com.kalabhedia.gimme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OnlineUserDataBase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "OnlineUser.db";
    public static final String TABLE_NAME = "VerifyTable";

    public static final String COL_1 = "PHONE_NUMBER";
    public static final String COL_2 = "RECEIVER_KEY";
    public static final String COL_3 = "VERIFIED_SUM";

    public OnlineUserDataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " TEXT, " + COL_2 + " TEXT," + COL_3 + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(String phoneNumber, String receiverKey, int verifiedSum) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;
        int exist = 0;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, phoneNumber);
        contentValues.put(COL_2, receiverKey);
        contentValues.put(COL_3, verifiedSum);
        Cursor cr = getAllData();
        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                if ((numberTemp.equals(phoneNumber))) {
                    exist = 1;
                    break;
                }
                cr.moveToNext();
            }
            if (exist == 0)
                result = db.insert(TABLE_NAME, null, contentValues);
        }
        else
            result = db.insert(TABLE_NAME, null, contentValues);

        if (result >= 0)
            return true;
        else
            return false;
//        long result = db.insert(TABLE_NAME, null, contentValues);
//
//        int id = (int) db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
//        if (id == -1) {
//            db.update("your_table", contentValues, "_id=?", new String[] {"1"});  // number 1 is the _id here, update to variable for your code
//        }
//
//        db.close();
//
//
//        if (id == -1)
//            return false;
//        else
//            return true;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cr = db.rawQuery("Select * from " + TABLE_NAME, null);
        return cr;
    }


    public boolean deleteUser(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_1 + "=" + id, null) > 0;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS VerifyTable");
    }


    public boolean updateData(String id, String code1, int code2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, code1);
        contentValues.put(COL_3, code2);
        int result = db.update(TABLE_NAME, contentValues, COL_1 + "=?", new String[]{id});
        if (result > 0)
            return true;
        else
            return false;
    }
}
