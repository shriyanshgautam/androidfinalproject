package com.shriyansh.foodbasket.util;

/**
 * Created by shriyansh on 15/3/15.
 */
public class OrderFoodItem {
    private long foodId;
    String foodName;
    String foodDescrption;
    int foodServe;
    int foodVeg;
    double foodPrice;
    private int quantity;
    private String foodVariety;
    String foodImgUrl;

    public OrderFoodItem(){
        foodId=0;
        quantity=0;
        foodVariety="Normal";
        this.foodImgUrl="";
    }

    public OrderFoodItem(long foodId,String foodName,String foodDescrption,int foodServe,int foodVeg,double foodPrice,int quantity,String foodVariety,String foodImgUrl ){
        this.foodId=foodId;
        this.quantity=quantity;
        this.foodVariety=foodVariety;
        this.foodName=foodName;
        this.foodDescrption=foodDescrption;
        this.foodServe=foodServe;
        this.foodVeg=foodVeg;
        this.foodPrice=foodPrice;
        this.foodImgUrl=foodImgUrl;
    }

    public long getFoodId(){
        return this.foodId;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public String getFoodVariety(){
        return foodVariety;
    }
    public String getFoodName(){
        return this.foodName;
    }
    public String getFoodDescrption(){
        return foodDescrption;
    }
    public int getFoodServe(){
        return foodServe;
    }
    public int getFoodVeg(){
        return foodVeg;
    }
    public double getFoodPrice(){
        return foodPrice;
    }
    public String getFoodImgUrl(){return  foodImgUrl;}

    public void setFoodPrice(double foodPrice){
        this.foodPrice=foodPrice;
    }
    public void setQuantity(int quantity){
        this.quantity=quantity;
    }

    @Override
    public String toString() {
        return this.foodId + ". " + this.quantity + " [$" + this.foodVariety + "]";
    }

}
