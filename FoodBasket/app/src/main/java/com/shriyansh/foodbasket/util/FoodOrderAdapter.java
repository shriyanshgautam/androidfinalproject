package com.shriyansh.foodbasket.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shriyansh.foodbasket.R;
import com.shriyansh.foodbasket.data.FoodContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by shriyansh on 15/3/15.
 */
public class FoodOrderAdapter extends ArrayAdapter<OrderFoodItem> {

    ViewHolder viewHolder;
    Context context;
    public FoodOrderAdapter(Context context, int textViewResourceId, ArrayList<OrderFoodItem> items) {
        super(context, textViewResourceId, items);
        this.context=context;
    }

    private static class ViewHolder {
        private TextView tvName,tvDescription,tvServe,tvVeg,tvPrice,tvQuantity;
        private ImageView foodImage;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.list_item_food, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.foodname);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.fooddesciption);
            viewHolder.tvServe = (TextView) convertView.findViewById(R.id.foodserves);
            viewHolder.tvVeg= (TextView) convertView.findViewById(R.id.foodveg);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.foodprice);
            viewHolder.tvQuantity = (TextView) convertView.findViewById(R.id.quantityfood);
            viewHolder.foodImage=(ImageView)convertView.findViewById(R.id.foodimage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OrderFoodItem item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.tvName.setText(item.getFoodName());
            viewHolder.tvDescription.setText(item.getFoodDescrption());
            viewHolder.tvServe.setText(item.getFoodServe()+" Serves");
            if(item.getFoodVeg()== FoodContract.FoodEntry.VALUE_TYPE_VEG_VEGETARIAN){
                viewHolder.tvVeg.setText("VEG");
            }else{
                viewHolder.tvVeg.setText("NON VEG");
            }

            viewHolder.tvPrice.setText("Rs. "+item.getFoodPrice());

            Uri newImageUri=Uri.parse(item.getFoodImgUrl());
            Log.d("IMGURI", newImageUri.toString());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(context.getContentResolver(), newImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            viewHolder.foodImage.setImageBitmap(bitmap);
            viewHolder.tvQuantity.setText(item.getQuantity()+"");

        }

        return convertView;
    }
}
