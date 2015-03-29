package com.shriyansh.foodbasket;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.shriyansh.foodbasket.data.FoodContract;

import java.io.IOException;

/**
 * Created by shriyansh on 14/3/15.
 */
public class OrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int LOADER_ID=1;

    ImageView takeOrder;


    SimpleCursorAdapter orderAdapter;
    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentValues values =new ContentValues();
        values.put(FoodContract.OrderEntry.COLUMN_CUSTOMER_NAME,"Shriyansh");
        values.put(FoodContract.OrderEntry.COLUMN_TABLE,2);
        values.put(FoodContract.OrderEntry.COLUMN_FOOD_ID,2);
        values.put(FoodContract.OrderEntry.COLUMN_FOOD_VARIANT_NAME,"Masala");
        values.put(FoodContract.OrderEntry.COLUMN_QUANTITY,3);
        values.put(FoodContract.OrderEntry.COLUMN__COMMENT,"nice service");
        values.put(FoodContract.OrderEntry.COLUMN_TIMESTAMP,10.20);
        values.put(FoodContract.OrderEntry.COLUMN_ORDER_TAKEN_BY,"Shriyansh");
        //getActivity().getContentResolver().insert(FoodContract.OrderEntry.buildOrderUri(), values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_order,container,false);

        orderAdapter= new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_order,
                null,
                new String[]{FoodContract.OrderEntry.COLUMN_CUSTOMER_NAME,
                        FoodContract.OrderEntry.COLUMN_TABLE,
                        FoodContract.OrderEntry.COLUMN__COMMENT,
                        FoodContract.OrderEntry.COLUMN_TIMESTAMP,
                        FoodContract.OrderEntry.COLUMN_IMG_URL},
                new int[]{R.id.customername,R.id.tablenumber,R.id.comment,R.id.timestamp,R.id.customerimage},0);



        orderAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            /** Binds the Cursor column defined by the specified index to the specified view */
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                if(view.getId() == R.id.customerimage){
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
                }
                return false;
            }
        });
        /*ImageView image = (ImageView)findViewById(R.id.news_image);
        viewBinder.setViewValue(image, cursor, cursor.getColumnIndex("image"));
        curAdapter.setViewBinder(viewBinder);
        mNewsView.setAdapter(curAdapter);*/

        ListView listView=(ListView)rootView.findViewById(R.id.list_order);
        listView.setAdapter(orderAdapter);

        takeOrder=(ImageView)rootView.findViewById(R.id.takeorder);
        takeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),CreateOrderActivity.class);
                startActivity(intent);
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // String forecast=foodAdapter.getItem(position);
                Intent intent=new Intent(getActivity(),OrderDetailActivity.class);
                Uri uri= FoodContract.OrderEntry.buildOrderUriWithId(id);
                Log.d("URI", uri.toString());
                Cursor cursor=getActivity().getContentResolver().query(uri,new String[]{FoodContract.OrderEntry.COLUMN_TIMESTAMP}, FoodContract.OrderEntry._ID+" = ?",new String[]{String.valueOf(id)},null);
                if(!cursor.moveToFirst()){return;}
                String time=cursor.getString(cursor.getColumnIndex(FoodContract.OrderEntry.COLUMN_TIMESTAMP));
                intent.putExtra(Intent.EXTRA_TEXT,time);
                startActivity(intent);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri= FoodContract.OrderEntry.buildOrderUri();
        Log.d("URI", uri.toString());

        return new CursorLoader(getActivity(),uri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        orderAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        orderAdapter.swapCursor(null);
    }

    public void toast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
