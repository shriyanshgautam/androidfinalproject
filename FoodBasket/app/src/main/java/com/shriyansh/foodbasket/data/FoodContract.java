package com.shriyansh.foodbasket.data;

/**
 * Created by shriyansh on 12/3/15.
 */


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class FoodContract {


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.shriyansh.foodbasket";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not. Don't be that dev, reader. Don't be that dev.
    public static final String PATH_ORDER = "order";
    public static final String PATH_ORDER_FOOD="order/food"; //by time
    public static final String PATH_ORDER_CUSTOMER="order/customer";
    public static final String PATH_ORDER_TABLE="order/table";
    public static final String PATH_ORDER_TAKEN_BY="order/taken_by";

    public static final String PATH_FOOD = "food";
    public static final String PATH_FOOD_CATEGORY="food/category"; //create two uris for this
    public static final String PATH_FOOD_AVAILABLE="food/available";
    public static final String PATH_FOOD_MAX_PREPARATION_TIME="food/mapreparation_time";
    public static final String PATH_FOOD_VEGNESS="food/veg";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    /*public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }*/

    /*
       Inner class that defines the table contents of the location table
        Students: This is where you will add the strings. (Similar to what has been
        done for WeatherEntry)
        Inner class that defines the contents of the location table
    */
    public static final class FoodEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOOD).build();
        public static final String CONTENT_TYPE =
               ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOOD;


        public static final String TABLE_NAME = "food";
        public static final String COLUMN_FOOD_NAME = "name";
        public static final String COLUMN_FOOD_DESCRIPTION="description";
        public static final String COLUMN_TYPE_VEG = "veg";
            public static final int VALUE_TYPE_VEG_VEGETARIAN=0;
            public static final int VALUE_TYPE_VER_NONVEGETARIAN=1;
        public static final String COLUMN_CATEGORY = "category";
            public static final int VALUE_CATEGORY_INDIAN = 1;
            public static final int VALUE_CATEGORY_SPANISH = 2;
            public static final int VALUE_CATEGORY_CHENEES= 3;
            public static final int VALUE_CATEGORY_THAI =4;
        //chiness,spanish,indian,thai,
        public static final String COLUMN_TYPE_SPICINESS = "spiciness";
            public static final int VALUE_TYPE_SPICINESS_SWEET=0;
            public static final int VALUE_TYPE_SPICINESS_MEDIUM=1;
            public static final int VALUE_TYPE_SPICINESS_SPICY=2;
        public static final String COLUMN_SERVES = "serves";
        public static final String COLUMN_BASE_PRICE = "base_price";
        public static final String COLUMN_MAIN_INGREDIENTS="main_ingredients";
        public static final String COLUMN_IMAGE_URL="image_url";
        public static final String COLUMN_AVAILABILITY="availability";
            public static final int VALUE_AVAILABILITY_AVAILABLE=1;
            public static final int VALUE_AVAILABILITY_UNAVAILABLE=0;
        public static final String COLUMN_TIME_TO_PREPARE="preparation_time";
        public static final String COLUMN_COMMENTS="comments";


        public static Uri buildFoodUriToReturn(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFoodUri(){
            return CONTENT_URI.buildUpon().build();
        }

        public static  Uri buildFoodWithIdUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromFoodUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static int getCategoryFromFoodUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
        public static int getPreparationTimeLimitFromFoodUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
        public static int getVegTypeFromFoodUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

    }


    /* Inner class that defines the table contents of the weather table */
    public static final class OrderEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;



        public static final String TABLE_NAME = "tbl_order";
        // Column with the foreign key into the location table.
        public static final String COLUMN_CUSTOMER_NAME = "customer_name";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_TABLE = "table_number";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_FOOD_ID = "food_id";
        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_FOOD_VARIANT_NAME="varient_name";
        public static final String COLUMN_QUANTITY = "quantity";
        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN__COMMENT = "comment";

        public static final String COLUMN_IMG_URL = "img_url";

        public static final String COLUMN_TIMESTAMP = "time";

        public static final String COLUMN_ORDER_TAKEN_BY = "order_taken_by";



        public static Uri buildOrderUriToReturn(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildOrderUri(){
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildOrderUriWithId(long id){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }


        public static Uri buildOrderWithFoodByTimeUri(String time){
            return CONTENT_URI.buildUpon().appendPath("food").appendPath(time).build();
        }



        /*
        Student: This is the buildWeatherLocation function you filled in.
        */
        /*public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }*/



        public static long getIdFromOrderUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getOrderTimeFromOrderwithFood(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static String getCustomerFromOrderUri(Uri uri){
            return uri.getPathSegments().get(2);
        }
        public static int gettableFromOrderUri( Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
        public static String getTakenByFromOrderUri(Uri uri){
            return  uri.getPathSegments().get(2);
        }


    }
}
