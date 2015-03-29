package com.shriyansh.foodbasket;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.shriyansh.foodbasket.data.FoodContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shriyansh on 15/3/15.
 */
public class CreateFoodFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    public CreateFoodFragment() {
        super();
    }



    EditText name,description,serves,basePrice,mainIngredinets,comments,timetoprepare;
    ImageView addbtn;
    Switch available,veg;
    ImageView foodImage,createNewFood;
    Spinner spinnerCategory,spinnerSpiciness;
    private static int TAKE_PICTURE = 1;
    private Uri imageUri;
    private Uri newImageUri=Uri.parse("");
    String imageTime;
    private int CROP_IMAGE=3;

    String sname,sdesc,scategory="0",sspiciness="0",sserves,sbaseprice,smainingredients,scomments,stime;
    int sveg,savailable;
    boolean submitted=false;

    public static final String FOOD_NAME="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_NAME";
    public static final String FOOD_DESCRIPTION="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_DESCRIPTION";
    public static final String FOOD_SERVES="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_SERVES";
    public static final String FOOD_PRICE="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_PRICE";
    public static final String FOOD_INGREDIENTS="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_INGREDIENTS";
    public static final String FOOD_COMMENT="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_COMMENT";
    public static final String FOOD_TIME="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_TIME";
    public static final String FOOD_CATEGORY="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_CATEGORY";
    public static final String FOOD_SPICINESS="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_SPICINESS";
    public static final String FOOD_IMG_URL="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_ING_URL";
    public static final String FOOD_AVAILABLE="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_AVAILABLE";
    public static final String FOOD_VEG="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_VEG";
    public static final String FOOD_SUBMITTED="com.shriyansh.foodbasket.CreateFoodActivity.FOOD_SUBMITTED";





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state

        savedInstanceState.putString(FOOD_NAME,name.getText().toString());
        savedInstanceState.putString(FOOD_DESCRIPTION, description.getText().toString());
        if(!serves.getText().toString().contentEquals("")){
            savedInstanceState.putInt(FOOD_SERVES, Integer.parseInt(serves.getText().toString()));
        }else{
            savedInstanceState.putInt(FOOD_SERVES,1);
        }
        if(!basePrice.getText().toString().contentEquals("")){
            savedInstanceState.putDouble(FOOD_PRICE, Double.parseDouble(basePrice.getText().toString()));
        }else{
            savedInstanceState.putDouble(FOOD_PRICE,0);
        }
        savedInstanceState.putString(FOOD_INGREDIENTS, mainIngredinets.getText().toString());
        savedInstanceState.putString(FOOD_COMMENT, comments.getText().toString());
        savedInstanceState.putString(FOOD_TIME,timetoprepare.getText().toString());
        savedInstanceState.putString(FOOD_IMG_URL,newImageUri.toString());

        if(!scategory.contentEquals("")){
            savedInstanceState.putInt(FOOD_CATEGORY,Integer.parseInt(scategory));
        }else{
            savedInstanceState.putInt(FOOD_CATEGORY,0);
        }

        if(!sspiciness.contentEquals("")){
            savedInstanceState.putInt(FOOD_SPICINESS, Integer.parseInt(sspiciness));
        }else{
            savedInstanceState.putInt(FOOD_SPICINESS,1);
        }
        savedInstanceState.putInt(FOOD_AVAILABLE, savailable);
        savedInstanceState.putInt(FOOD_VEG, sveg);
        savedInstanceState.putString(FOOD_IMG_URL, newImageUri.toString());
        savedInstanceState.putBoolean(FOOD_SUBMITTED,submitted);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_create_food,container,false);
        name=(EditText)rootView.findViewById(R.id.createname);
        description=(EditText)rootView.findViewById(R.id.createdesc);
        serves=(EditText)rootView.findViewById(R.id.createserves);
        basePrice=(EditText)rootView.findViewById(R.id.createbaseprice);
        mainIngredinets=(EditText)rootView.findViewById(R.id.createmainingredients);
        comments=(EditText)rootView.findViewById(R.id.createcomments);
        timetoprepare=(EditText)rootView.findViewById(R.id.createtime);
        addbtn=(ImageView)rootView.findViewById(R.id.tvaddbutton);
        available=(Switch)rootView.findViewById(R.id.createavailable);
        veg=(Switch)rootView.findViewById(R.id.createveg);
        foodImage=(ImageView)rootView.findViewById(R.id.foodimage);
        spinnerCategory=(Spinner)rootView.findViewById(R.id.spinnerCategory);
        spinnerSpiciness=(Spinner)rootView.findViewById(R.id.spinnerSpiciness);
        createNewFood=(ImageView)rootView.findViewById(R.id.tvcreatenew);

        if(savedInstanceState!=null){
            name.setText(savedInstanceState.getString(FOOD_NAME));
            description.setText(savedInstanceState.getString(FOOD_DESCRIPTION));
            serves.setText(savedInstanceState.getInt(FOOD_SERVES) + "");
            basePrice.setText(savedInstanceState.getDouble(FOOD_PRICE) + "");
            mainIngredinets.setText(savedInstanceState.getString(FOOD_INGREDIENTS));
            comments.setText(savedInstanceState.getString(FOOD_COMMENT));
            timetoprepare.setText(savedInstanceState.getString(FOOD_TIME));
            spinnerCategory.setSelection(savedInstanceState.getInt(FOOD_CATEGORY));
            spinnerSpiciness.setSelection(savedInstanceState.getInt(FOOD_SPICINESS));
            available.setChecked(savedInstanceState.getInt(FOOD_AVAILABLE, savailable) == 1);
            veg.setChecked(savedInstanceState.getInt(FOOD_VEG, sveg) == FoodContract.FoodEntry.VALUE_TYPE_VEG_VEGETARIAN);
            submitted=savedInstanceState.getBoolean(FOOD_SUBMITTED);
            if(submitted){
                addbtn.setBackgroundColor(getResources().getColor(R.color.green));
            }
            Uri imgUri=Uri.parse(savedInstanceState.getString(FOOD_IMG_URL));
            newImageUri=imgUri;
            if(newImageUri.toString().contentEquals("")){
                foodImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
            }else{
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(getActivity().getContentResolver(), imgUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                foodImage.setImageBitmap(bitmap);
            }





        }


        sveg= FoodContract.FoodEntry.VALUE_TYPE_VER_NONVEGETARIAN;
        savailable= FoodContract.FoodEntry.VALUE_AVAILABILITY_UNAVAILABLE;


        // Spinner Drop down elements
        List<String> categoriesData = new ArrayList<String>();
        categoriesData.add("Starters");
        categoriesData.add("MainCourse");
        categoriesData.add("Snacks");
        categoriesData.add("Desserts");

        // Spinner Drop down elements
        List<String> spicinessData = new ArrayList<String>();
        spicinessData.add("Sweet");
        spicinessData.add("Medium");
        spicinessData.add("Spicy");

        // Creating adapter for spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriesData);
        ArrayAdapter<String> spicinessAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spicinessData);

        // Drop down layout style - list view with radio button
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spicinessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerSpiciness.setAdapter(spicinessAdapter);





        available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    savailable= FoodContract.FoodEntry.VALUE_AVAILABILITY_AVAILABLE;

                }else{
                    savailable= FoodContract.FoodEntry.VALUE_AVAILABILITY_UNAVAILABLE;

                }
            }
        });

        veg.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sveg= FoodContract.FoodEntry.VALUE_TYPE_VEG_VEGETARIAN;

                }else{
                    sveg= FoodContract.FoodEntry.VALUE_TYPE_VER_NONVEGETARIAN;

                }
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


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(submitted){
                    toast("Food Already Added!");
                }else{
                    sname=name.getText().toString();
                    sdesc=description.getText().toString();
                    sserves=serves.getText().toString();
                    sbaseprice=basePrice.getText().toString();
                    smainingredients=mainIngredinets.getText().toString();
                    scomments=comments.getText().toString();
                    stime=timetoprepare.getText().toString();
                    if (sname.contentEquals("") || sserves.contentEquals("")||sbaseprice.contentEquals("")||stime.contentEquals("")){
                        Toast.makeText(getActivity(),"Please fill the required details!",Toast.LENGTH_SHORT).show();
                    }else{
                        //save
                        ContentValues values =new ContentValues();
                        Uri uri= FoodContract.FoodEntry.buildFoodUri();
                        values.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME,sname);
                        values.put(FoodContract.FoodEntry.COLUMN_FOOD_DESCRIPTION,sdesc);
                        values.put(FoodContract.FoodEntry.COLUMN_TYPE_VEG, sveg);
                        values.put(FoodContract.FoodEntry.COLUMN_CATEGORY, Integer.valueOf(scategory));
                        values.put(FoodContract.FoodEntry.COLUMN_TYPE_SPICINESS, Integer.valueOf(sspiciness));
                        values.put(FoodContract.FoodEntry.COLUMN_SERVES, Integer.valueOf(sserves));
                        values.put(FoodContract.FoodEntry.COLUMN_BASE_PRICE, Double.valueOf(sbaseprice));
                        values.put(FoodContract.FoodEntry.COLUMN_MAIN_INGREDIENTS,smainingredients);
                        values.put(FoodContract.FoodEntry.COLUMN_AVAILABILITY, savailable);
                        values.put(FoodContract.FoodEntry.COLUMN_IMAGE_URL,newImageUri.toString());
                        values.put(FoodContract.FoodEntry.COLUMN_TIME_TO_PREPARE, Double.valueOf(stime));
                        values.put(FoodContract.FoodEntry.COLUMN_COMMENTS,scomments);

                        Uri fooduri=getActivity().getContentResolver().insert(uri, values);
                        toast("Added Successfully!");

                        //getActivity().finish();
                        addbtn.setBackgroundColor(getResources().getColor(R.color.green));
                        submitted=true;
                        createNewFood.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        createNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("");
                description.setText("");
                serves.setText("");
                basePrice.setText("");
                mainIngredinets.setText("");
                comments.setText("");
                timetoprepare.setText("");
                spinnerCategory.setSelection(0);
                spinnerSpiciness.setSelection(0);
                available.setChecked(false);
                veg.setChecked(false);
                addbtn.setBackgroundColor(getResources().getColor(R.color.grey));
                createNewFood.setVisibility(View.INVISIBLE);
                foodImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
                newImageUri=Uri.parse("");
                submitted=false;

            }
        });

        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==TAKE_PICTURE && resultCode== Activity.RESULT_OK){
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


    public String getTimeStringForImage(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public void takePhoto(View view,String time) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), time+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        newImageUri=Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/a" + time + ".jpg");
        Log.e("IMG_URI", imageUri.toString());
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

    public void toast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(view.getId()==R.id.spinnerSpiciness){
            sspiciness=String.valueOf(position);
        }else if(view.getId()==R.id.spinnerCategory){
            scategory=String.valueOf(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}
