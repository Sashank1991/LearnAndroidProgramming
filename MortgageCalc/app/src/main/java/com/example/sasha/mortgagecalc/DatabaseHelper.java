package com.example.sasha.mortgagecalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by sasha on 3/19/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Property.db";
    private static final int DATABASE_VERSION = 10;
    public static final String TABLE_NAME = "preference";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_type = "type";
    public static final String COLUMN_streetAdd = "streetAdd";
    public static final String COLUMN_city = "city";
    public static final String COLUMN_lat = "lat";
    public static final String COLUMN_long = "long";
    public static final String COLUMN_loneAmt = "loneAmt";
    public static final String COLUMN_apr = "apr";
    public static final String COLUMN_monthlyPayment = "monthlyPayment";
    public static final String COLUMN_propertyPrice = "propertyPrice";
    public static final String COLUMN_downPayment = "downPayment";
    public static final String COLUMN_term = "term";
    public static final String COLUMN_state = "state";
    public static final String COLUMN_zip = "zip";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_type + " TEXT, " +
                COLUMN_streetAdd + " TEXT, " +
                COLUMN_city + " TEXT, " +
                COLUMN_lat + " TEXT, " +
                COLUMN_long + " TEXT, " +
                COLUMN_loneAmt + " TEXT, " +
                COLUMN_apr + " TEXT, " +
                COLUMN_monthlyPayment + " TEXT, " +
                COLUMN_propertyPrice + " TEXT, " +
                COLUMN_downPayment + " TEXT, " +
                COLUMN_term + " TEXT, " +
                COLUMN_state + " TEXT, " +
                COLUMN_zip + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //String type, String streetAdd, String city,Double loneAmt,Double apr,Double lat,Double lng,Double monthlyPayment
    public boolean insertProperty(List<String> _data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_type, _data.get(0));
        contentValues.put(COLUMN_streetAdd, _data.get(1));
        contentValues.put(COLUMN_city, _data.get(2));
        contentValues.put(COLUMN_loneAmt, _data.get(3));
        contentValues.put(COLUMN_apr, _data.get(4));
        contentValues.put(COLUMN_lat, _data.get(5));
        contentValues.put(COLUMN_long, _data.get(6));
        contentValues.put(COLUMN_monthlyPayment, _data.get(7));
        contentValues.put(COLUMN_propertyPrice, _data.get(8));
        contentValues.put(COLUMN_downPayment, _data.get(9));
        contentValues.put(COLUMN_term, _data.get(10));
        contentValues.put(COLUMN_state, _data.get(11));
        contentValues.put(COLUMN_zip, _data.get(12));
        Long inserter = db.insert(TABLE_NAME, null, contentValues);

        return true;
    }

    // Integer id, String type, String streetAdd, String city, Double loneAmt, Double apr, Double lat, Double lng, Double monthlyPayment
    public boolean updateProperty(List<String> _data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_type, _data.get(1));
        contentValues.put(COLUMN_streetAdd, _data.get(2));
        contentValues.put(COLUMN_city, _data.get(3));
        contentValues.put(COLUMN_loneAmt, _data.get(4));
        contentValues.put(COLUMN_apr, _data.get(5));
        contentValues.put(COLUMN_lat, _data.get(6));
        contentValues.put(COLUMN_long, _data.get(7));
        contentValues.put(COLUMN_monthlyPayment, _data.get(8));
        contentValues.put(COLUMN_propertyPrice, _data.get(9));
        contentValues.put(COLUMN_downPayment, _data.get(10));
        contentValues.put(COLUMN_term, _data.get(11));
        contentValues.put(COLUMN_state, _data.get(12));
        contentValues.put(COLUMN_zip, _data.get(13));
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[]{_data.get(0)});
        return true;
    }

    public Cursor getProperty(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllProperties() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public Integer deleteProperty(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
}
