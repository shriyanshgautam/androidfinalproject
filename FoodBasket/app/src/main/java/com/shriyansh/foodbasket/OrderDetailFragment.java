package com.shriyansh.foodbasket;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shriyansh.foodbasket.data.FoodContract;

import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Created by shriyansh on 14/3/15.
 */
public class OrderDetailFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    public static final int LOADER_ID=2;

    public double totalPrice=0;
    SimpleCursorAdapter orderFoodAdapter;
    TextView tvTimestamp,tvTableNuber,tvCustomerName,tvComment,tvTotalPrice;
    ImageView customerImage;
    SharedPreferences myprefs;
    String orederTaker;
    double taxRate;

    public OrderDetailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        myprefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        orederTaker=myprefs.getString(getString(R.string.string_order_taken_by_key),getString(R.string.string_order_taken_by_default));
        taxRate=Double.valueOf(myprefs.getString(getString(R.string.string_tax_key),getString(R.string.string_tax_default)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_order_detail,container,false);
        Intent intent=getActivity().getIntent();
        Long orderId=intent.getLongExtra(Intent.EXTRA_TEXT,0);
        //tv=(TextView)rootView.findViewById(R.id.order_detail_tv);
        //tv.setText(orderId+"");
        tvTimestamp=(TextView)rootView.findViewById(R.id.ordrtime);
        tvTableNuber=(TextView)rootView.findViewById(R.id.tablenumber);
        tvCustomerName=(TextView)rootView.findViewById(R.id.customername);
        tvComment=(TextView)rootView.findViewById(R.id.comment);
        tvTotalPrice=(TextView)rootView.findViewById(R.id.totalprice);
        customerImage=(ImageView)rootView.findViewById(R.id.customerimage);

        orderFoodAdapter= new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_food,
                null,
                new String[]{FoodContract.FoodEntry.TABLE_NAME+"."+FoodContract.FoodEntry.COLUMN_FOOD_NAME,
                        FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_FOOD_DESCRIPTION,
                        FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_SERVES,
                        FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_TYPE_VEG,
                        FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_BASE_PRICE,
                        FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_IMAGE_URL,
                        FoodContract.OrderEntry.TABLE_NAME+"."+ FoodContract.OrderEntry.COLUMN_QUANTITY},
                new int[]{R.id.foodname,R.id.fooddesciption,R.id.foodserves,R.id.foodveg,R.id.foodprice,R.id.foodimage,R.id.quantityfood},0);


        orderFoodAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
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
                    int quantity=cursor.getInt(5);
                    double price=cursor.getDouble(columnIndex);
                    ((TextView)view).setText("Rs. "+price*quantity);
                    return true;
                }
                return false;
            }
        });

        ListView listView=(ListView)rootView.findViewById(R.id.order_food_list);
        listView.setAdapter(orderFoodAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent=getActivity().getIntent();
        String orderTime=intent.getStringExtra(Intent.EXTRA_TEXT);
        Uri uri= FoodContract.OrderEntry.buildOrderWithFoodByTimeUri(orderTime);
        Log.d("URI", uri.toString());

        return new CursorLoader(getActivity(),uri,null,null,null,null);

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()){return;}
        String name=data.getString(data.getColumnIndex(FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_FOOD_NAME));

        String person=data.getString(data.getColumnIndex(FoodContract.OrderEntry.TABLE_NAME+"."+ FoodContract.OrderEntry.COLUMN_CUSTOMER_NAME));
        String table=data.getString(data.getColumnIndex(FoodContract.OrderEntry.TABLE_NAME+"."+ FoodContract.OrderEntry.COLUMN_TABLE));
        String timestamp=data.getString(data.getColumnIndex(FoodContract.OrderEntry.TABLE_NAME+"."+ FoodContract.OrderEntry.COLUMN_TIMESTAMP));
        String comment=data.getString(data.getColumnIndex(FoodContract.OrderEntry.TABLE_NAME+"."+ FoodContract.OrderEntry.COLUMN__COMMENT));
        tvTableNuber.setText(table);
        tvCustomerName.setText(person);
        tvTimestamp.setText(timestamp);
        tvComment.setText(comment);
        orderFoodAdapter.swapCursor(data);

        //setting image
        String url=data.getString(data.getColumnIndex(FoodContract.OrderEntry.COLUMN_IMG_URL));
        Uri newImageUri=Uri.parse(url);
        Log.d("IMGURI",newImageUri.toString());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media
                    .getBitmap(getActivity().getContentResolver(), newImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        customerImage.setImageBitmap(bitmap);

        //counting total amount
        data.moveToFirst();
       do{
           totalPrice+=data.getDouble(data.getColumnIndex(FoodContract.FoodEntry.TABLE_NAME+"."+ FoodContract.FoodEntry.COLUMN_BASE_PRICE))*
           data.getInt(data.getColumnIndex(FoodContract.OrderEntry.TABLE_NAME+"."+ FoodContract.OrderEntry.COLUMN_QUANTITY));
       }while(data.moveToNext());
        double showPrice=totalPrice+totalPrice*taxRate/100;
        tvTotalPrice.setText(""+showPrice);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        orderFoodAdapter.swapCursor(null);
    }


}
