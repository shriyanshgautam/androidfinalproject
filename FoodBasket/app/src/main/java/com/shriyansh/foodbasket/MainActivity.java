package com.shriyansh.foodbasket;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.shriyansh.foodbasket.data.FoodContract;
import com.shriyansh.foodbasket.sync.FoodBasketSyncAdapter;


public class MainActivity extends ActionBarActivity implements OrderFragment.Callback{

    boolean mTwoPane=false;
    public static String ORDER_DETAIL_FRAG_TAG="ODFT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.order_container)!=null){

            mTwoPane=true;


            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.order_container, new OrderDetailFragment(), ORDER_DETAIL_FRAG_TAG)
                        .commit();
                //default opening of first order in detail in two pane
                Uri uri= FoodContract.OrderEntry.buildOrderUriWithId(5);
                //querying for order clicked
                Cursor cursor=getContentResolver().query(uri,new String[]{FoodContract.OrderEntry.COLUMN_TIMESTAMP}, FoodContract.OrderEntry._ID+" = ?",new String[]{String.valueOf(5)},null);
                if(!cursor.moveToFirst()){return;}
                //taking out the time
                String time=cursor.getString(cursor.getColumnIndex(FoodContract.OrderEntry.COLUMN_TIMESTAMP));
                Bundle args=new Bundle();
                args.putString(OrderFragment.ORDER_TIME_URI, time);
                OrderDetailFragment orderDetailFragment=new OrderDetailFragment();
                orderDetailFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.order_container, orderDetailFragment, ORDER_DETAIL_FRAG_TAG)
                        .commit();


            }
        }else{
            mTwoPane=false;
        }
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.order_container, new OrderFragment())
                    .commit();
        }*/

        FoodBasketSyncAdapter.initializeSyncAdapter(MainActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }else if(id==R.id.action_food){
            Intent intent=new Intent(MainActivity.this,FoodActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri orderUri,long id) {

        //querying for order clicked
        Cursor cursor=getContentResolver().query(orderUri,new String[]{FoodContract.OrderEntry.COLUMN_TIMESTAMP}, FoodContract.OrderEntry._ID+" = ?",new String[]{String.valueOf(id)},null);
        if(!cursor.moveToFirst()){return;}
        //taking out the time
        String time=cursor.getString(cursor.getColumnIndex(FoodContract.OrderEntry.COLUMN_TIMESTAMP));

        if(mTwoPane){
            Bundle args=new Bundle();
            args.putString(OrderFragment.ORDER_TIME_URI, time);
            OrderDetailFragment orderDetailFragment=new OrderDetailFragment();
            orderDetailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.order_container, orderDetailFragment, ORDER_DETAIL_FRAG_TAG)
                    .commit();


        }else{
            //calling order detail from that time
            Intent intent=new Intent(MainActivity.this,OrderDetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,time);
            startActivity(intent);
        }


    }
}
