package com.shriyansh.foodbasket.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.shriyansh.foodbasket.MainActivity;
import com.shriyansh.foodbasket.R;
import com.shriyansh.foodbasket.data.FoodContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by shriyansh on 28/3/15.
 */
public class FoodBasketSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = FoodBasketSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 1;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    public static final String CURRENT_FOOD_VERSION="current_food_version";
    SharedPreferences myprefs;
    SharedPreferences.Editor editor;

    public FoodBasketSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        myprefs= PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");
        int version = myprefs.getInt(CURRENT_FOOD_VERSION,0);//Utility.getPreferredLocation(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String foodJsonStr = null;

//        String format = "json";
//        String units = "metric";
//        int numDays = 14;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://foodbasket.innovaders.in/food.php?";
            final String QUERY_PARAM = "q";
//            final String FORMAT_PARAM = "mode";
//            final String UNITS_PARAM = "units";
//            final String DAYS_PARAM = "cnt";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, String.valueOf(version))
//                    .appendQueryParameter(FORMAT_PARAM, format)
//                    .appendQueryParameter(UNITS_PARAM, units)
//                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            foodJsonStr = buffer.toString();
            getFoodDataFromJson(foodJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;



    }


    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getFoodDataFromJson(String forecastJsonStr
                                        )
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.



        // Location information
        final String FOOD_ITEMS="food_items";
        final String FOOD_NAME = "name";
        final String FOOD_DESC = "description";
        final String FOOD_TYPE = "type";
        final String FOOD_CAT="category";
        final String FOOD_SPICINESS="spiciness";
        final String FOOD_SERVES="serves";
        final String FOOD_PRICE="price";
        final String FOOD_INGREDIENTS="ingredients";
        final String FOOD_AVAILABILITY="availability";
        final String FOOD_IMAGE="image";
        final String FOOD_TIME="time";
        final String FOOD_COMMENTS="comments";
        final String FOOD_VERSION="food_version";

        String snames="New Food items : ";

        try {
            JSONObject foodJson = new JSONObject(forecastJsonStr);
            JSONArray foodArray = foodJson.getJSONArray(FOOD_ITEMS);
            int newVersion=foodJson.getInt(FOOD_VERSION);
            editor=myprefs.edit();
            editor.putInt(CURRENT_FOOD_VERSION,newVersion);
            editor.commit();
//
//            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
//            String cityName = cityJson.getString(OWM_CITY_NAME);
//
//            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
//            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
//            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

            //long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(foodArray.length());

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();

            for (int i = 0; i < foodArray.length(); i++) {
                // These are the values that will be collected.

                String sname, sdesc, scategory = "0", sspiciness = "0", sserves, sbaseprice, smainingredients, scomments, stime, simgurl;
                int sveg, savailable;

//            long dateTime;
//                double pressure;
//                int humidity;
//                double windSpeed;
//                double windDirection;
//
//                double high;
//                double low;
//
//                String description;
//                int weatherId;

                // Get the JSON object representing the day
                JSONObject foodItem = foodArray.getJSONObject(i);

                // Cheating to convert this to UTC time, which is what we want anyhow
                sname = foodItem.getString(FOOD_NAME);
                snames+=sname+" ";
                sdesc = foodItem.getString(FOOD_DESC);
                sveg = foodItem.getInt(FOOD_TYPE);
                scategory = foodItem.getString(FOOD_CAT);
                sspiciness = foodItem.getString(FOOD_SPICINESS);
                sserves = foodItem.getString(FOOD_SERVES);
                sbaseprice = foodItem.getString(FOOD_PRICE);
                smainingredients = foodItem.getString(FOOD_INGREDIENTS);
                savailable = foodItem.getInt(FOOD_AVAILABILITY);
                simgurl = foodItem.getString(FOOD_IMAGE);
                stime = foodItem.getString(FOOD_TIME);
                scomments = foodItem.getString(FOOD_COMMENTS);


//                pressure = dayForecast.getDouble(OWM_PRESSURE);
//                humidity = dayForecast.getInt(OWM_HUMIDITY);
//                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
//                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
//
//                // Description is in a child array called "weather", which is 1 element long.
//                // That element also contains a weather code.
//                JSONObject weatherObject =
//                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//                weatherId = weatherObject.getInt(OWM_WEATHER_ID);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                high = temperatureObject.getDouble(OWM_MAX);
//                low = temperatureObject.getDouble(OWM_MIN);

                ContentValues values = new ContentValues();


                values.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, sname);
                values.put(FoodContract.FoodEntry.COLUMN_FOOD_DESCRIPTION, sdesc);
                values.put(FoodContract.FoodEntry.COLUMN_TYPE_VEG, sveg);
                values.put(FoodContract.FoodEntry.COLUMN_CATEGORY, Integer.valueOf(scategory));
                values.put(FoodContract.FoodEntry.COLUMN_TYPE_SPICINESS, Integer.valueOf(sspiciness));
                values.put(FoodContract.FoodEntry.COLUMN_SERVES, Integer.valueOf(sserves));
                values.put(FoodContract.FoodEntry.COLUMN_BASE_PRICE, Double.valueOf(sbaseprice));
                values.put(FoodContract.FoodEntry.COLUMN_MAIN_INGREDIENTS, smainingredients);
                values.put(FoodContract.FoodEntry.COLUMN_AVAILABILITY, savailable);
                values.put(FoodContract.FoodEntry.COLUMN_IMAGE_URL, simgurl);
                values.put(FoodContract.FoodEntry.COLUMN_TIME_TO_PREPARE, Double.valueOf(stime));
                values.put(FoodContract.FoodEntry.COLUMN_COMMENTS, scomments);

                cVVector.add(values);

            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                Uri uri = FoodContract.FoodEntry.buildFoodUri();
                getContext().getContentResolver().bulkInsert(uri, cvArray);

                //Uri fooduri=getContext().getContentResolver().insert(uri, values);

                // delete old data so we don't build up an endless history
//                getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
//                        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
//                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

                notifyFood(snames);
                Log.d(LOG_TAG, "Sync Called Now");

            }

                //for notification
                Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

    }

    private void notifyFood(String names) {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        */
        //if ( displayNotifications ) {

            //String lastNotificationKey = context.getString(R.string.pref_last_notification);
            //long lastSync = prefs.getLong(lastNotificationKey, 0);

            //if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                //String locationQuery = Utility.getPreferredLocation(context);

                //Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());

                // we'll query our contentProvider, as always
                //Cursor cursor = context.getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

                /*if (cursor.moveToFirst()) {
                    int weatherId = cursor.getInt(INDEX_WEATHER_ID);
                    double high = cursor.getDouble(INDEX_MAX_TEMP);
                    double low = cursor.getDouble(INDEX_MIN_TEMP);
                    String desc = cursor.getString(INDEX_SHORT_DESC);

                    int iconId = Utility.getIconResourceForWeatherCondition(weatherId);

                    Bitmap largeIcon = BitmapFactory.decodeResource(resources,
                            Utility.getArtResourceForWeatherCondition(weatherId));
                    String title = context.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText = String.format(context.getString(R.string.format_notification),
                            desc,
                            Utility.formatTemperature(context, high),
                            Utility.formatTemperature(context, low));
                */
                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                    Resources resources = context.getResources();
                    String title = context.getString(R.string.app_name);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setColor(resources.getColor(R.color.grey))
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    //.setLargeIcon(R.drawable.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(names);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    /*SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();*/
               // }
                //cursor.close();
            //}
    }




    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        FoodBasketSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }



}
