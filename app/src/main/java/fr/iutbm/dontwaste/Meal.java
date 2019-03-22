package fr.iutbm.dontwaste;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "meals")
public class Meal {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "mealname")
    private String mealName;

    @ColumnInfo(name = "bmppath")
    private String bmppath;

    @ColumnInfo(name = "price")
    private float price;

    @ColumnInfo(name = "userName")
    private String userName;

    @ColumnInfo(name = "latitude")
    private float latitude;

    @ColumnInfo(name = "longitude")
    private float longitude;


    public Meal(String mealName, String bmppath, float price, String userName, float latitude, float longitude) {
        this.mealName = mealName;
        this.bmppath = bmppath;
        this.price = price;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Ignore
    public Meal(int uid, String mealName, String bmppath, float price, String userName, float latitude, float longitude) {
        this.uid = uid;
        this.mealName = mealName;
        this.bmppath = bmppath;
        this.price = price;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getBmppath() {
        return bmppath;
    }

    public void setBmppath(String bmppath) {
        this.bmppath = bmppath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
