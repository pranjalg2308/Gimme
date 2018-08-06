package com.kalabhedia.gimme;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Transaction.db";
    public static final String TABLE_NAME = "User_table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "MONEY";
    public static final String COL_4 = "CLAIM_REASON";
    public static final String COL_5 = "CODE_CLAIM_USER_1";
    public static final String COL_6 = "CODE_CLAIM_USER_2";
    public static final String COL_7 = "CLAIM_SENT";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " TEXT, " + COL_2 + " TEXT," + COL_3 + " INTEGER," + COL_4 + " TEXT," + COL_5 + " INTEGER," + COL_6 + " INTEGER," + COL_7 + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(String time, String name, String reason, String money, String claim_user_1, String claim_user_2) {
        SQLiteDatabase db = this.getWritableDatabase();
        int exist = 0;
        long result = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, time);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, money);
        contentValues.put(COL_4, reason);
        contentValues.put(COL_5, claim_user_1);
        contentValues.put(COL_6, claim_user_2);
        contentValues.put(COL_7, 0);
        Cursor cr = getAllData();
        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                if ((numberTemp.equals(time))) {
                    exist = 1;
                    break;
                }
                cr.moveToNext();
            }
            if (exist == 0)
                result = db.insert(TABLE_NAME, null, contentValues);
            else
                updateData(time, claim_user_1, claim_user_2);
        } else
            result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cr = db.rawQuery("Select * from " + TABLE_NAME, null);
        return cr;
    }

    public boolean updateData(String id, String code1, String code2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, code1);
        contentValues.put(COL_6, code2);
        int result = db.update(TABLE_NAME, contentValues, COL_1 + "=?", new String[]{id});
        if (result > 0)
            return true;
        else
            return false;
    }

//    public boolean updateOnSent(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_7, 1);
//        int result = db.update(TABLE_NAME, contentValues, COL_1 + "=?", new String[]{id});
//        if (result > 0)
//            return true;
//        else
//            return false;
//    }

    public void deleteUserData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cr = getUserData(name);
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                if (cr.getString(1).equals(name))
                    db.delete(TABLE_NAME, COL_1 + "=" + cr.getString(0), null);

                cr.moveToNext();
            }
        }
    }
    public Cursor getUserData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME + " where " + COL_2 + " = '" + name + "'";
        Cursor cr = db.rawQuery(query, null);
        return cr;
    }

    public int getVerifiedSum(String name) {
        Cursor cr = getUserData(name);
        int sum = 0;
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                if ((cr.getString(4) + cr.getString(5)).equals("11"))
                sum = sum + cr.getInt(2);
                cr.moveToNext();
            }
        }
        return sum;
    }


}

