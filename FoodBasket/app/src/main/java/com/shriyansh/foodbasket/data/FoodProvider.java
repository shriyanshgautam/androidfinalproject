package com.shriyansh.foodbasket.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.shriyansh.foodbasket.data.FoodContract;

/**
 * Created by shriyansh on 14/3/15.
 */
public class FoodProvider extends ContentProvider {

        private static final UriMatcher sUriMatcher = buildUriMatcher();
        private FoodDbHelper mDbHelper;

    static final int FOOD = 101;
    static final int FOOD_WITH_ID = 102;
    static final int FOOD_CATEGORY = 103;
    static final int FOOD_WITH_A_CATEGORY=104;
    static final int FOOD_WITH_AVAILABILITY = 105;
    static final int FOOD_WITH_PREP_TIME=106;
    static final int FOOD_WITH_VEGNESS=107;

    static final int ORDER=201;
    static final int ORDER_WITH_ID=202;
    static final int ORDER_WITH_FOOD=203; //by time
    static final int ORDER_WITH_CUSTOMER=204;
    static final int ORDER_WITH_TABLE=205;
    static final int ORDER_WITH_TAKEN_BY=206;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;


    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //order INNER JOIN food ON order.food = food._id
        sWeatherByLocationSettingQueryBuilder.setTables(
                FoodContract.OrderEntry.TABLE_NAME + " INNER JOIN " +
                        FoodContract.FoodEntry.TABLE_NAME +
                        " ON " + FoodContract.OrderEntry.TABLE_NAME +
                        "." + FoodContract.OrderEntry.COLUMN_FOOD_ID +
                        " = " + FoodContract.FoodEntry.TABLE_NAME +
                        "." + FoodContract.FoodEntry._ID);
    }

    //foods by order_id
    private static final String sFoodsByOrderIdSelection =
            FoodContract.OrderEntry.TABLE_NAME+
                    "." + FoodContract.OrderEntry.COLUMN_TIMESTAMP + " = ? ";

    //location.location_setting = ? AND date >= ?
    /*private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";
    */
    //location.location_setting = ? AND date = ?
    /*private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";
    */

    //by time
    private Cursor getOrderFood(Uri uri, String[] projection, String sortOrder) {
        String orderTime = FoodContract.OrderEntry.getOrderTimeFromOrderwithFood(uri);


        String[] selectionArgs;
        String selection;

        if (orderTime.contentEquals("")) {
            selection = null;
            selectionArgs = null;
        } else {
            selectionArgs = new String[]{orderTime};
            selection = sFoodsByOrderIdSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFoodCategories(Uri uri, String[] projection, String sortOrder) {
        return mDbHelper.getWritableDatabase().query(FoodContract.FoodEntry.TABLE_NAME,new String[]{FoodContract.FoodEntry.COLUMN_CATEGORY},null,null, FoodContract.FoodEntry.COLUMN_CATEGORY,null,sortOrder);
    }

    private Cursor getFoodById(Uri uri, String[] projection, String sortOrder) {
        long FoodId = FoodContract.FoodEntry.getIdFromFoodUri(uri);
        String selection= FoodContract.FoodEntry._ID+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(FoodId)};
        return mDbHelper.getWritableDatabase().query(FoodContract.FoodEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getFoodByCategory(Uri uri, String[] projection, String sortOrder) {
        int category= FoodContract.FoodEntry.getCategoryFromFoodUri(uri);
        String selection= FoodContract.FoodEntry.COLUMN_CATEGORY+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(category)};
        return mDbHelper.getWritableDatabase().query(FoodContract.FoodEntry.TABLE_NAME,projection,selection,projection,null,null,sortOrder);
    }

    private Cursor getAvailableFood(Uri uri, String[] projection, String sortOrder) {
        String selection= FoodContract.FoodEntry.COLUMN_AVAILABILITY+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(FoodContract.FoodEntry.VALUE_AVAILABILITY_AVAILABLE)};
        return mDbHelper.getWritableDatabase().query(FoodContract.FoodEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getFoodWithPrepTime(Uri uri, String[] projection, String sortOrder) {
        int time= FoodContract.FoodEntry.getPreparationTimeLimitFromFoodUri(uri);
        String selection= FoodContract.FoodEntry.COLUMN_TIME_TO_PREPARE+" <= ?  ";
        String[] selectionArgs=new String[]{String.valueOf(time)};
        return mDbHelper.getWritableDatabase().query(FoodContract.FoodEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getFoodByVegness(Uri uri, String[] projection, String sortOrder) {
        int vegness= FoodContract.FoodEntry.getVegTypeFromFoodUri(uri);
        String selection= FoodContract.FoodEntry.COLUMN_TYPE_VEG+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(vegness)};
        return mDbHelper.getWritableDatabase().query(FoodContract.FoodEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getOrderById(Uri uri, String[] projection, String sortOrder) {
        long orderId= FoodContract.OrderEntry.getIdFromOrderUri(uri);
        String selection= FoodContract.OrderEntry._ID+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(orderId)};
        return mDbHelper.getWritableDatabase().query(FoodContract.OrderEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getOrderByCustomerName(Uri uri, String[] projection, String sortOrder) {
        String customerName= FoodContract.OrderEntry.getCustomerFromOrderUri(uri);
        String selection= FoodContract.OrderEntry.COLUMN_CUSTOMER_NAME+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(customerName)};
        return mDbHelper.getWritableDatabase().query(FoodContract.OrderEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getOrderByTable(Uri uri, String[] projection, String sortOrder) {
        int table= FoodContract.OrderEntry.gettableFromOrderUri(uri);
        String selection= FoodContract.OrderEntry.COLUMN_TABLE+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(table)};
        return mDbHelper.getWritableDatabase().query(FoodContract.OrderEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }
    private Cursor getOrderByTakenBy(Uri uri, String[] projection, String sortOrder) {
        String takenBy= FoodContract.OrderEntry.getTakenByFromOrderUri(uri);
        String selection= FoodContract.OrderEntry.COLUMN_ORDER_TAKEN_BY+" = ? ";
        String[] selectionArgs=new String[]{String.valueOf(takenBy)};
        return mDbHelper.getWritableDatabase().query(FoodContract.OrderEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }



    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FoodContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, FoodContract.PATH_FOOD,FOOD); //whole table dir
        matcher.addURI(authority,FoodContract.PATH_FOOD+"/#",FOOD_WITH_ID); //with id  item
        matcher.addURI(authority,FoodContract.PATH_FOOD_CATEGORY,FOOD_CATEGORY); //all categories  dir
        matcher.addURI(authority,FoodContract.PATH_FOOD_CATEGORY+"/#",FOOD_WITH_A_CATEGORY); //food from single category item
        matcher.addURI(authority,FoodContract.PATH_FOOD_AVAILABLE,FOOD_WITH_AVAILABILITY); //getavailable food  dir
        matcher.addURI(authority,FoodContract.PATH_FOOD_MAX_PREPARATION_TIME+"/*",FOOD_WITH_PREP_TIME);//food with time less tahn this  dir
        matcher.addURI(authority,FoodContract.PATH_FOOD_VEGNESS+"/*",FOOD_WITH_VEGNESS);//get food veg or non veg  dir

        matcher.addURI(authority, FoodContract.PATH_ORDER,ORDER); //all orders dir
        matcher.addURI(authority,FoodContract.PATH_ORDER+"/#",ORDER_WITH_ID); //order by id
        matcher.addURI(authority,FoodContract.PATH_ORDER_FOOD+"/*",ORDER_WITH_FOOD); //all foods of one order by order time dir
        matcher.addURI(authority,FoodContract.PATH_ORDER_CUSTOMER+"/*",ORDER_WITH_CUSTOMER);//get order by customer name
        matcher.addURI(authority,FoodContract.PATH_ORDER_TABLE+"/#",ORDER_WITH_TABLE);//get order by table number
        matcher.addURI(authority,FoodContract.PATH_ORDER_TAKEN_BY+"/*",ORDER_WITH_TAKEN_BY); //get orders taken by taker name


       //matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        //matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        //matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        //matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);*/
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper= new FoodDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            //"food categories"
            case FOOD_CATEGORY:
                retCursor=getFoodCategories(uri,projection,sortOrder);
                break;
            case FOOD_WITH_ID:
                retCursor=getFoodById(uri,projection,sortOrder);
                break;
            case FOOD_WITH_A_CATEGORY:
                retCursor=getFoodByCategory(uri, projection, sortOrder);
                break;
            case FOOD_WITH_AVAILABILITY:
                retCursor=getAvailableFood(uri,projection,sortOrder);
                break;
            case FOOD_WITH_PREP_TIME:
                retCursor=getFoodWithPrepTime(uri,projection,sortOrder);
                break;
            case FOOD_WITH_VEGNESS:
                retCursor=getFoodByVegness(uri,projection,sortOrder);
                break;
            case ORDER_WITH_ID:
                retCursor=getOrderById(uri,projection,sortOrder);
                break;
            case ORDER_WITH_FOOD:
                retCursor=getOrderFood(uri, projection, sortOrder);
                break;
            case ORDER_WITH_CUSTOMER:
                retCursor=getOrderByCustomerName(uri,projection,sortOrder);
                break;
            case ORDER_WITH_TABLE:
                retCursor=getOrderByTable(uri,projection,sortOrder);
                break;
            case ORDER_WITH_TAKEN_BY:
                retCursor=getOrderByTakenBy(uri,projection,sortOrder);
                break;

            // "food"
            case FOOD: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        FoodContract.FoodEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "order"
            case ORDER: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        FoodContract.OrderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        FoodContract.OrderEntry.COLUMN_TIMESTAMP,
                        null,
                        FoodContract.FoodEntry._ID+" DESC"
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case FOOD:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case FOOD_WITH_ID:
                return FoodContract.FoodEntry.CONTENT_ITEM_TYPE;
            case FOOD_CATEGORY:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case FOOD_WITH_A_CATEGORY:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case FOOD_WITH_AVAILABILITY:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case FOOD_WITH_PREP_TIME:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case FOOD_WITH_VEGNESS:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case ORDER:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case ORDER_WITH_ID:
                return FoodContract.FoodEntry.CONTENT_ITEM_TYPE;
            case ORDER_WITH_FOOD:
                return FoodContract.FoodEntry.CONTENT_TYPE;
            case ORDER_WITH_CUSTOMER:
                return FoodContract.FoodEntry.CONTENT_ITEM_TYPE;
            case ORDER_WITH_TABLE:
                return FoodContract.FoodEntry.CONTENT_ITEM_TYPE;
            case ORDER_WITH_TAKEN_BY:
                return FoodContract.FoodEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FOOD: {
                long _id = db.insert(FoodContract.FoodEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = FoodContract.FoodEntry.buildFoodUriToReturn(_id);
                    Log.d("INSERTION", returnUri.toString());
                }else
                    throw new android.database.SQLException("Failed to insert row into " + uri+" "+values.toString()+" "+db.rawQuery("SELECT * FROM "+ FoodContract.FoodEntry.TABLE_NAME,null).getCount());
                break;
            }
            case ORDER: {
                long _id = db.insert(FoodContract.OrderEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = FoodContract.OrderEntry.buildOrderUriToReturn(_id);

                    Log.d("INSERTION", returnUri.toString());
                }else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( selection == null ) selection = "1";
        switch (match) {
            case FOOD:
                rowsDeleted = db.delete(
                        FoodContract.FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ORDER:
                rowsDeleted = db.delete(
                        FoodContract.FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FOOD:

                rowsUpdated = db.update(FoodContract.FoodEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ORDER:
                rowsUpdated = db.update(FoodContract.FoodEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOOD:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(FoodContract.FoodEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }



}
