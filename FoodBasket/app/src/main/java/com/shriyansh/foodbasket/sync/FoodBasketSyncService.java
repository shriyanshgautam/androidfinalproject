package com.shriyansh.foodbasket.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by shriyansh on 28/3/15.
 */
public class FoodBasketSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static FoodBasketSyncAdapter foodBasketSyncAdapter=null;

    @Override
    public void onCreate() {
        Log.d("FoodBasketSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (foodBasketSyncAdapter == null) {
                foodBasketSyncAdapter = new FoodBasketSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return foodBasketSyncAdapter.getSyncAdapterBinder();
    }
}
