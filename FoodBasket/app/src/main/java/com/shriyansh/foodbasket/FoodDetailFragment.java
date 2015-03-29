package com.shriyansh.foodbasket;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.shriyansh.foodbasket.data.FoodContract;
import com.shriyansh.foodbasket.data.FoodContract.FoodEntry.*;

import java.io.IOException;

/**
 * Created by shriyansh on 14/3/15.
 */
public class FoodDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID=3;


    public static final String[] projection={
            FoodContract.FoodEntry._ID,
            FoodContract.FoodEntry.COLUMN_FOOD_NAME,
            FoodContract.FoodEntry.COLUMN_FOOD_DESCRIPTION,
            FoodContract.FoodEntry.COLUMN_TYPE_VEG,
            FoodContract.FoodEntry.COLUMN_CATEGORY,
            FoodContract.FoodEntry.COLUMN_TYPE_SPICINESS,
            FoodContract.FoodEntry.COLUMN_SERVES,
            FoodContract.FoodEntry.COLUMN_BASE_PRICE,
            FoodContract.FoodEntry.COLUMN_MAIN_INGREDIENTS,
            FoodContract.FoodEntry.COLUMN_AVAILABILITY,
            FoodContract.FoodEntry.COLUMN_TIME_TO_PREPARE,
            FoodContract.FoodEntry.COLUMN_COMMENTS,
            FoodContract.FoodEntry.COLUMN_IMAGE_URL

    };

    public static final int COL_FOOD_ID=0;
    public static final int COL_NAME=1;
    public static final int COL_DESCRIPTION=2;
    public static final int COL_VEG=3;
    public static final int COL_CATEGORY=4;
    public static final int COL_SPICINESS=5;
    public static final int COL_SERVES=6;
    public static final int COL_BASE_PRICE=7;
    public static final int COL_INGEDIENTS=8;
    public static final int COL_AVALABILITY=9;
    public static final int COL_TIME_TO_PREPARE=10;
    public static final int COL_COMMENTS=11;
    public static final int COL_IMG_URL=12;


    SimpleCursorAdapter orderAdapter;
    TextView tvfname,tvfdescription,tvfcategory,tvfspiciness,tvfserves,tvfmainIngredients,tvfComments,tvfTime,tvfPrice;
    Switch swavailability,swveg;
    ImageView foodImage;

    public FoodDetailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_food_detail,container,false);
        tvfname=(TextView)rootView.findViewById(R.id.foodname);
        tvfdescription=(TextView)rootView.findViewById(R.id.fooddesciption);
        tvfcategory=(TextView)rootView.findViewById(R.id.foodcategory);
        tvfspiciness=(TextView)rootView.findViewById(R.id.foodspiciness);
        tvfserves=(TextView)rootView.findViewById(R.id.foodserves);
        tvfmainIngredients=(TextView)rootView.findViewById(R.id.foodmainingredients);
        tvfComments=(TextView)rootView.findViewById(R.id.foodcomments);
        tvfTime=(TextView)rootView.findViewById(R.id.foodtime);
        tvfPrice=(TextView)rootView.findViewById(R.id.foodprice);
        foodImage=(ImageView)rootView.findViewById(R.id.foodimage);
        swavailability=(Switch)rootView.findViewById(R.id.switchavailability);
        swveg=(Switch)rootView.findViewById(R.id.switchveg);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent=getActivity().getIntent();
        Long orderId=intent.getLongExtra(Intent.EXTRA_TEXT,0);
        Uri uri= FoodContract.FoodEntry.buildFoodWithIdUri(orderId);
        Log.d("URI FOOD DETAIL", uri.toString());

        return new CursorLoader(getActivity(),uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()){return;}
        String name,descrption,veg,categroy,spiciness,serves,price,ingrdients,time,availability,comments,imgurl;
                name=data.getString(COL_NAME);
                descrption=data.getString(COL_DESCRIPTION);
                veg=data.getString(COL_VEG);
                categroy=data.getString(COL_CATEGORY);
                spiciness=data.getString(COL_SPICINESS);
                serves=data.getString(COL_SERVES);
                price=data.getString(COL_BASE_PRICE);
                ingrdients=data.getString(COL_INGEDIENTS);
                time=data.getString(COL_TIME_TO_PREPARE);
                availability=data.getString(COL_AVALABILITY);
                comments=data.getString(COL_COMMENTS);
                imgurl=data.getString(COL_IMG_URL);

        String detail=name+descrption+veg+categroy+spiciness+serves+price+ingrdients+time+availability+comments;
        //setting image

        tvfname.setText(name);
        tvfdescription.setText(descrption);
        if(Integer.parseInt(availability)==1){
            swavailability.toggle();
        }
        if(Integer.parseInt(veg)== FoodContract.FoodEntry.VALUE_TYPE_VEG_VEGETARIAN){
            swveg.toggle();
        }
        switch(Integer.valueOf(categroy)){
            case 1:tvfcategory.setText("Starters");
                break;
            case 2:tvfcategory.setText("Main Course");
                break;
            case 3:tvfcategory.setText("Snacks");
                break;
            case 4:tvfcategory.setText("Desserts");
                break;
        }
        switch(Integer.valueOf(spiciness)){
            case 1:tvfspiciness.setText("Sweet");
                break;
            case 2:tvfspiciness.setText("Medium");
                break;
            case 3:tvfspiciness.setText("Spicy");
                break;
        }
        tvfserves.setText(serves+" Serves");
        tvfmainIngredients.setText(ingrdients);
        tvfComments.setText(comments);
        tvfTime.setText(time+" minutes");
        tvfPrice.setText("Rs. "+price);



        Uri newImageUri=Uri.parse(imgurl);
        Log.d("IMGURI",newImageUri.toString());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media
                    .getBitmap(getActivity().getContentResolver(), newImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        foodImage.setImageBitmap(bitmap);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
