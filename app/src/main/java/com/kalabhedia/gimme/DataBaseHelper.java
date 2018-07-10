package com.kalabhedia.gimme;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.os.Build.ID;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Transaction.db";
    public static final String TABLE_NAME = "User_table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "MONEY";
    public static final String COL_4 = "CLAIM_REASON";
    public static final String COL_5 = "CODE_CLAIM_USER_1";
    public static final String COL_6 = "CODE_CLAIM_USER_2";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + COL_2 + " TEXT," + COL_3 + " INTEGER," + COL_4 + " TEXT," + COL_5 + " INTEGER," + COL_6 + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(String name, String reason, String money, String claim_user_1, String claim_user_2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, money);
        contentValues.put(COL_4, reason);
        contentValues.put(COL_5, claim_user_1);
        contentValues.put(COL_6, claim_user_2);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cr=db.rawQuery("Select * from "+TABLE_NAME,null);
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

}

