package com.shriyansh.foodbasket;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shriyansh.foodbasket.data.FoodContract;
import com.shriyansh.foodbasket.util.FoodOrderAdapter;
import com.shriyansh.foodbasket.util.OrderFoodItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by shriyansh on 15/3/15.
 */
public class CreateOrderfragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int LOADER_ID=5;

    public static final String EXTRA_FOOD_ID="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_ID";
    public static final String EXTRA_FOOD_NAME="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_NAME";
    public static final String EXTRA_FOOD_DESCRIPTION="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_DESCRPTION";
    public static final String EXTRA_FOOD_SERVE="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_SERVE";
    public static final String EXTRA_FOOD_VEG="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_VEG";
    public static final String EXTRA_FOOD_PRICE="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_PRICE";
    public static final String EXTRA_FOOD_QUANTITY="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_QUANTITY";
    public static final String EXTRA_FOOD_VARIETY="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_VARIETY";
    public static final String EXTRA_FOOD_IMG_URL="com.shriyansh.foodbasket.CreateOrderActivity.FOOD_MG_URL";

    public static final String CUSTOMER_NAME="com.shriyansh.foodbasket.CreateOrderActivity.CUSROMER_NAME";
    public static final String CUSTOMER_TABLE="com.shriyansh.foodbasket.CreateOrderActivity.CUSROMER_TABLE";
    public static final String CUSTOMER_COMMENT="com.shriyansh.foodbasket.CreateOrderActivity.CUSROMER_COMMENT";



    FoodOrderAdapter adapter;
    ArrayList<OrderFoodItem> itemList;
    SimpleCursorAdapter orderFoodAdapter;
    Double totalPrice=0.0;
    TextView tvtotalprice,tvorderTime;
    ImageView insertFoodItem,submitOrder;
    EditText etTableNumber,etName,etComment;
    ImageView foodImage;
    int tableNumber=0;
    String customerName,Comment,orderTime;
    private static int TAKE_PICTURE = 1;
    private Uri imageUri;
    private Uri newImageUri=Uri.parse("");
    String imageTime;
    private int CROP_IMAGE=3;
    SharedPreferences myprefs;
    String orederTaker;
    double taxRate;



    public CreateOrderfragment() {
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
        double showPrice=totalPrice+totalPrice*taxRate/100;
        tvtotalprice.setText("Rs. "+showPrice);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state

        savedInstanceState.putString(CUSTOMER_NAME,etName.getText().toString());
        if(etTableNumber.getText().toString().contentEquals("")){

        }else{
            savedInstanceState.putInt(CUSTOMER_TABLE,Integer.parseInt(etTableNumber.getText().toString()));
        }

        savedInstanceState.putString(CUSTOMER_COMMENT, etComment.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_create_order,container,false);

       orderFoodAdapter= new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_food,
                null,
                new String[]{FoodContract.FoodEntry.TABLE_NAME+"."+FoodContract.FoodEntry.COLUMN_FOOD_NAME},
                new int[]{R.id.foodname},0);
        insertFoodItem=(ImageView)rootView.findViewById(R.id.insertfood);
        tvtotalprice=(TextView)rootView.findViewById(R.id.totalprice);
        tvtotalprice.setText("Rs. "+totalPrice.toString());
        submitOrder=(ImageView)rootView.findViewById(R.id.submitOrder);
        etTableNumber=(EditText)rootView.findViewById(R.id.tablenumber);
        etName=(EditText)rootView.findViewById(R.id.customername);
        etComment=(EditText)rootView.findViewById(R.id.comment);
        tvorderTime=(TextView)rootView.findViewById(R.id.ordrtime);
        foodImage=(ImageView)rootView.findViewById(R.id.foodimage);

        if(savedInstanceState!=null){
            etName.setText(savedInstanceState.getString(CUSTOMER_NAME));
            etTableNumber.setText(savedInstanceState.getInt(CUSTOMER_TABLE)+"");
            etComment.setText(savedInstanceState.getString(CUSTOMER_COMMENT));
        }


        orderTime=getTimeString();
        tvorderTime.setText(orderTime);

        insertFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectFoodActivity.class);
                startActivityForResult(intent, 11);
            }
        });

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time=getTimeStringForImage();
                imageTime=time;
                takePhoto(foodImage,time);
            }
        });

        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tableNumber=Integer.parseInt(etTableNumber.getText().toString());
                }catch (NumberFormatException e){
                    tableNumber=0;
                    Toast.makeText(getActivity(),"Table number not valid",Toast.LENGTH_SHORT).show();
                }

                customerName=etName.getText().toString();
                Comment=etComment.getText().toString();
                if(tableNumber==0 ||customerName.contentEquals("")){
                    Toast.makeText(getActivity(),"Please check the fields",Toast.LENGTH_SHORT).show();
                }else if(itemList.size()==0){
                    Toast.makeText(getActivity(),"Please Add Food to your basket",Toast.LENGTH_SHORT).show();
                }else if(newImageUri.toString().contentEquals("")){
                    Toast.makeText(getActivity(),"Image not taken",Toast.LENGTH_SHORT).show();
                }else{

                    //insert
                    Iterator itr=itemList.iterator();
                    while (itr.hasNext()){
                       OrderFoodItem item= (OrderFoodItem)itr.next();
                        ContentValues values =new ContentValues();

                        values.put(FoodContract.OrderEntry.COLUMN_CUSTOMER_NAME,customerName);
                        values.put(FoodContract.OrderEntry.COLUMN_TABLE,tableNumber);
                        values.put(FoodContract.OrderEntry.COLUMN_FOOD_ID,item.getFoodId());
                        values.put(FoodContract.OrderEntry.COLUMN_FOOD_VARIANT_NAME,item.getFoodVariety());
                        values.put(FoodContract.OrderEntry.COLUMN_QUANTITY,item.getQuantity());
                        values.put(FoodContract.OrderEntry.COLUMN_IMG_URL,newImageUri.toString());
                        values.put(FoodContract.OrderEntry.COLUMN__COMMENT,Comment);
                        values.put(FoodContract.OrderEntry.COLUMN_TIMESTAMP,orderTime);
                        values.put(FoodContract.OrderEntry.COLUMN_ORDER_TAKEN_BY,orederTaker);

                        getActivity().getContentResolver().insert(FoodContract.OrderEntry.buildOrderUri(), values);
                    }

                  //go back
                    getActivity().finish();

                }
            }
        });

        ListView listView=(ListView)rootView.findViewById(R.id.order_food_list);
        itemList = new ArrayList<OrderFoodItem>();


        adapter = new FoodOrderAdapter(getActivity(),
                R.layout.list_item_food, itemList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrderFoodItem item=itemList.get(position);
                itemList.remove(position);
                double initialPrice=item.getFoodPrice();
                int initialQuantity=item.getQuantity();
                double basePrice=initialPrice/initialQuantity;
                int finalQuantity=initialQuantity+1;
                item.setQuantity(finalQuantity);
                item.setFoodPrice(basePrice*finalQuantity);
                itemList.add(position,item);
                adapter.notifyDataSetChanged();
                totalPrice+=basePrice;
                double showPrice=totalPrice+totalPrice*taxRate/100;
                tvtotalprice.setText("Rs. "+showPrice);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                OrderFoodItem item=itemList.get(position);
                itemList.remove(position);
                double initialPrice=item.getFoodPrice();
                int initialQuantity=item.getQuantity();
                double basePrice=initialPrice/initialQuantity;

                if(initialQuantity>1){
                    int finalQuantity=initialQuantity-1;
                    item.setQuantity(finalQuantity);
                    item.setFoodPrice(basePrice*finalQuantity);
                    itemList.add(position,item);
                    adapter.notifyDataSetChanged();
                    totalPrice-=basePrice;
                    double showPrice=totalPrice+totalPrice*taxRate/100;
                    tvtotalprice.setText("Rs. "+showPrice);

                }else{

                    //remove item

                    adapter.notifyDataSetChanged();
                    totalPrice-=basePrice;
                    double showPrice=totalPrice+totalPrice*taxRate/100;
                    tvtotalprice.setText("Rs. "+showPrice);

                    return true;
                }

                return true;
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==11 && resultCode==Activity.RESULT_OK){
            String name,description,variety,imgUrl;
            long id;
            int serve,veg,quantity;
            double price;

            id=data.getLongExtra(EXTRA_FOOD_ID,0);
            name=data.getStringExtra(EXTRA_FOOD_NAME);
            description=data.getStringExtra(EXTRA_FOOD_DESCRIPTION);
            serve=data.getIntExtra(EXTRA_FOOD_SERVE, 1);
            veg=data.getIntExtra(EXTRA_FOOD_VEG,0);
            price=data.getDoubleExtra(EXTRA_FOOD_PRICE, 0.0);
            quantity=1;
            variety="Normal";
            imgUrl=data.getStringExtra(EXTRA_FOOD_IMG_URL);

            //itemList.add(new OrderFoodItem(id,name,description,serve,veg,price,quantity,variety));

            if(checkExistence(id)){
                Toast.makeText(getActivity(),"Item Already Exists",Toast.LENGTH_SHORT).show();
            }else{
                adapter.add(new OrderFoodItem(id,name,description,serve,veg,price,quantity,variety,imgUrl));
                adapter.notifyDataSetChanged();
                totalPrice+=price;
                double showPrice=totalPrice+totalPrice*taxRate/100;
                tvtotalprice.setText("Rs. "+showPrice);

            }


        }else if(requestCode==11 && resultCode==Activity.RESULT_CANCELED){


        }else if(requestCode==TAKE_PICTURE && resultCode==Activity.RESULT_OK){
            Uri selectedImage = imageUri;
            getActivity().getContentResolver().notifyChange(selectedImage, null);

            ContentResolver cr = getActivity().getContentResolver();
            Bitmap bitmap;
            try {
                //resizeImage(imageUri.toString(),imageTime);
                performCropImage(imageUri,imageTime);
                Toast.makeText(getActivity(), selectedImage.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                        .show();
                Log.e("Camera", e.toString());
            }

        }else if(requestCode==CROP_IMAGE && resultCode==Activity.RESULT_OK){
            ContentResolver cr = getActivity().getContentResolver();
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(cr, newImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            foodImage.setImageBitmap(bitmap);
        }
    }
    public String getTimeString(){
            SimpleDateFormat sdfDate = new SimpleDateFormat("E, MMM d HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            return strDate;

    }

    public String getTimeStringForImage(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    public boolean checkExistence(long id){
       Iterator itr= itemList.iterator();
        while(itr.hasNext()){
           OrderFoodItem item= (OrderFoodItem)itr.next();
            if(id==item.getFoodId()){
                return true;
            }


        }
        return false;
    }

    public void takePhoto(View view,String time) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), time+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        newImageUri=Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/a" + time + ".jpg");
        Log.e("IMG_URI",imageUri.toString());
        Log.e("IMG_URI",newImageUri.toString());
        startActivityForResult(intent, TAKE_PICTURE);
    }





    private Uri mCropImagedUri;
    /**Crop the image
     * @return returns <tt>true</tt> if crop supports by the device,otherwise false*/
    private boolean performCropImage(Uri imgUri,String timeString ){
        try {
            if(imgUri!=null){
                //call the standard crop action intent (the user device may not support it)
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                //indicate image type and Uri
                cropIntent.setDataAndType(imgUri, "image/*");
                //set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("scale", true);
                //indicate output X and Y
                cropIntent.putExtra("outputX", 500);
                cropIntent.putExtra("outputY", 500);
                //retrieve data on return
                cropIntent.putExtra("return-data", false);

                File f = createNewFile("",timeString);
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
                //start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, CROP_IMAGE);
                return true;
            }
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return false;
    }

    private File createNewFile(String prefix,String time){
        if(prefix==null || "".equalsIgnoreCase(prefix)){
            prefix="";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory()+"");
        if(!newDirectory.exists()){
            if(newDirectory.mkdir()){
                Log.d(getActivity().getClass().getName(), newDirectory.getAbsolutePath()+" directory created");
            }
        }
        File file = new File(newDirectory,("a"+time+".jpg"));

        if(file.exists()){
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

}
