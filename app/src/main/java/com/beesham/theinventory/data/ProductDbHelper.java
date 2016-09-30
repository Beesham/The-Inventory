package com.beesham.theinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Beesham on 9/27/2016.
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "inventory.db";
    private final static int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_MANUFACTURER + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_MANUFACTURER_PHONE + " TEXT,"
                + ProductContract.ProductEntry.COLUMN_MANUFACTURER_EMAIL + " TEXT" + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
