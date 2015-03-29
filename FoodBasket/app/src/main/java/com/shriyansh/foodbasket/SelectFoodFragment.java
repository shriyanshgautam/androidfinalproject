package com.shriyansh.foodbasket;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shriyansh.foodbasket.CreateOrderfragment.*;

import com.shriyansh.foodbasket.data.FoodContract;

import java.io.IOException;

/**
 * Created by shriyansh on 15/3/15.
 */
public class SelectFoodFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID=4;

    public static final String[] projection={
            FoodContract.FoodEntry._ID,
            FoodContract.FoodEntry.COLUMN_FOOD_NAME,
            FoodContract.FoodEntry.COLUMN_FOOD_DESCRIPTION,
            FoodContract.FoodEntry.COLUMN_TYPE_VEG,
            FoodContract.FoodEntry.COLUMN_SERVES,
            FoodContract.FoodEntry.COLUMN_BASE_PRICE,
            FoodContract.FoodEntry.COLUMN_AVAILABILITY,
            FoodContract.FoodEntry.COLUMN_IMAGE_URL,

    };

    public static final int COL_FOOD_ID=0;
    public static final int COL_NAME=1;
    public static final int COL_DESCRIPTION=2;
    public static final int COL_VEG=3;
    public static final int COL_SERVES=4;
    public static final int COL_BASE_PRICE=5;
    public static final int COL_AVALABILITY=6;
    public static final int COL_IMG_URL=7;




    SimpleCursorAdapter foodAdapter;
    public SelectFoodFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_food, container, false);
        foodAdapter= new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_food,
                null,
                new String[]{FoodContract.FoodEntry.COLUMN_FOOD_NAME,
                         FoodContract.FoodEntry.COLUMN_FOOD_DESCRIPTION,
                        FoodContract.FoodEntry.COLUMN_SERVES,
                        FoodContract.FoodEntry.COLUMN_TYPE_VEG,
                        FoodContract.FoodEntry.COLUMN_BASE_PRICE,
                        FoodContract.FoodEntry.COLUMN_IMAGE_URL},
                new int[]{R.id.foodname,R.id.fooddesciption,R.id.foodserves,R.id.foodveg,R.id.foodprice,R.id.foodimage},0);


        foodAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            /** Binds the Cursor column defined by the specified index to the specified view */
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                if(view.getId() == R.id.foodimage){
                    String url=cursor.getString(columnIndex);
                    Uri newImageUri=Uri.parse(url);
                    Log.d("IMGURI",newImageUri.toString());
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media
                                .getBitmap(getActivity().getContentResolver(), newImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ((ImageView)view).setImageBitmap(bitmap);
                    return true; //true because the data was bound to the view
                }else if(view.getId()==R.id.foodserves){
                    int serves=cursor.getInt(columnIndex);
                    ((TextView)view).setText(serves+" Serves");
                    return true;
                }else if(view.getId()==R.id.foodveg){
                    int veg=cursor.getInt(columnIndex);
                    if(veg== FoodContract.FoodEntry.VALUE_TYPE_VEG_VEGETARIAN){
                        ((TextView)view).setText("VEG");
                    }else{
                        ((TextView)view).setText("NON VEG");
                    }
                    return true;
                }else if(view.getId()==R.id.foodprice){
                    double price=cursor.getDouble(columnIndex);
                    ((TextView)view).setText("Rs. "+price);
                    return true;
                }
                return false;
            }
        });

        ListView listView=(ListView)rootView.findViewById(R.id.foodlist);
        listView.setAdapter(foodAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // String forecast=foodAdapter.getItem(position);
                //return the position
                Cursor cursor=getActivity().getContentResolver().query(FoodContract.FoodEntry.buildFoodWithIdUri(id),projection,null,null,null);
                if(!cursor.moveToFirst()){
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();}

                Intent intent = getActivity().getIntent();
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_ID, cursor.getLong(COL_FOOD_ID));
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_NAME,cursor.getString(COL_NAME));
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_DESCRIPTION, cursor.getString(COL_DESCRIPTION));
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_SERVE, cursor.getInt(COL_SERVES));
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_VEG, cursor.getInt(COL_VEG));
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_PRICE, cursor.getDouble(COL_BASE_PRICE));
                //intent.putExtra(CreateOrderfragment.EXTRA_FOOD_QUANTITY, 5);
                //intent.putExtra(CreateOrderfragment.EXTRA_FOOD_VARIETY,"Normal");
                intent.putExtra(CreateOrderfragment.EXTRA_FOOD_IMG_URL,cursor.getString(COL_IMG_URL));
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri= FoodContract.FoodEntry.buildFoodUri();
        Log.d("URI", uri.toString());

        return new CursorLoader(getActivity(),uri,null,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        foodAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        foodAdapter.swapCursor(null);
    }
}
