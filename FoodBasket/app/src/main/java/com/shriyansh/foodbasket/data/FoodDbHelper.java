package com.shriyansh.foodbasket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.shriyansh.foodbasket.data.FoodContract.*;

/**
 * Created by shriyansh on 12/3/15.
 */
public class FoodDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 17;
    static final String DATABASE_NAME = "food.db";

    private Context context;
    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold locations. A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_FOOD_TABLE = "CREATE TABLE " + FoodEntry.TABLE_NAME + " (" +
                FoodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                FoodEntry.COLUMN_FOOD_NAME + " TEXT NOT NULL , "+
                FoodEntry.COLUMN_FOOD_DESCRIPTION + " TEXT,  " +
                FoodEntry.COLUMN_TYPE_VEG + " INTEGER NOT NULL , " +
                FoodEntry.COLUMN_CATEGORY + " INTEGER , "+
                FoodEntry.COLUMN_TYPE_SPICINESS + " INTEGER , "+
                FoodEntry.COLUMN_SERVES + " INTEGER NOT NULL, "+
                FoodEntry.COLUMN_BASE_PRICE + " REAL NOT NULL , "+
                FoodEntry.COLUMN_MAIN_INGREDIENTS + " TEXT , "+
                FoodEntry.COLUMN_IMAGE_URL + " TEXT , "+
                FoodEntry.COLUMN_AVAILABILITY + " INTEGER NOT NULL , "+
                FoodEntry.COLUMN_TIME_TO_PREPARE + " REAL NOT NULL , "+
                FoodEntry.COLUMN_COMMENTS + " TEXT "+
                " );";


        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + OrderEntry.TABLE_NAME + " (" +

                OrderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +

                OrderEntry.COLUMN_CUSTOMER_NAME + " TEXT NOT NULL , " +
                OrderEntry.COLUMN_TABLE + " INTEGER NOT NULL , " +
                OrderEntry.COLUMN_FOOD_ID + " INTEGER NOT NULL , " +
                OrderEntry.COLUMN_FOOD_VARIANT_NAME + " TEXT , " +
                OrderEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                OrderEntry.COLUMN__COMMENT + " TEXT , " +
                OrderEntry.COLUMN_TIMESTAMP + " TEXT NOT NULL , " +
                OrderEntry.COLUMN_IMG_URL+" TEXT , "+
                OrderEntry.COLUMN_ORDER_TAKEN_BY + " TEXT NOT NULL, " +

                // Set up the food_id column as a foreign key to location table.
                " FOREIGN KEY (" + OrderEntry.COLUMN_FOOD_ID + ") REFERENCES " +
                 FoodEntry.TABLE_NAME + " (" + FoodEntry._ID + ") ); " ;


        sqLiteDatabase.execSQL(SQL_CREATE_FOOD_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_TABLE);

        Log.d("TBL FOOD",SQL_CREATE_FOOD_TABLE);
        Log.d("TBL FOOD",SQL_CREATE_ORDER_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {


        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FoodEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
