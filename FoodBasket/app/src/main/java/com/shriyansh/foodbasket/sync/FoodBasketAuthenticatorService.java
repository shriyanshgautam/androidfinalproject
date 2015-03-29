package com.shriyansh.foodbasket.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by shriyansh on 28/3/15.
 */
public class FoodBasketAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private FoodBasketAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new FoodBasketAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
