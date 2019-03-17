package fr.iutbm.dontwaste;

public class Meal {
    private int uid;
    private String mealName;
    private String bmppath;
    private float price;
    private float latitude;
    private float longitude;

    public Meal(int uid, String mealName, String bmppath, float price, float latitude, float longitude) {
        this.uid = uid;
        this.mealName = mealName;
        this.bmppath = bmppath;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Meal(String mealName, String bmppath, float price, float latitude, float longitude) {
        this.mealName = mealName;
        this.bmppath = bmppath;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getUid() {
        return uid;
    }

    public String getMealName() {
        return mealName;
    }

    public String getBmppath() {
        return bmppath;
    }

    public float getPrice() {
        return price;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
