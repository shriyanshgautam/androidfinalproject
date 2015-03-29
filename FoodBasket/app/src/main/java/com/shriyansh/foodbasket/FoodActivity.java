package com.shriyansh.foodbasket;

import android.content.Intent;
import android.database.Cursor;
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

import com.shriyansh.foodbasket.data.FoodContract;


public class FoodActivity extends ActionBarActivity implements FoodFragment.Callback{

    boolean mTwoPane=false;
    public static final String FOOD_DETAIL_FRAG_TAG="FDFT";
    public static final String EXTRA_FOOD_ID="com.shriyansh.foodbasket.Foodactivity.EXTRA_FOOD_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        if(findViewById(R.id.food_container)!=null){

            mTwoPane=true;

            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.food_container,new FoodDetailFragment(),FOOD_DETAIL_FRAG_TAG)
                        .commit();
            }
        }else{
            mTwoPane=false;
        }
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FoodFragment())
                    .commit();
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
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
            Intent intent=new Intent(FoodActivity.this,SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(long id) {

        if(mTwoPane){
            Bundle args=new Bundle();
            args.putLong(FoodFragment.FOOD_ITEM_ID, id);
            FoodDetailFragment foodDetailFragment=new FoodDetailFragment();
            foodDetailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.food_container, foodDetailFragment, FOOD_DETAIL_FRAG_TAG)
                    .commit();


        }else{
            //calling order detail from that time
            Intent intent=new Intent(FoodActivity.this,FoodDetailActivity.class);
            intent.putExtra(EXTRA_FOOD_ID,id);
            startActivity(intent);
        }
    }
}
