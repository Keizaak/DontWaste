package fr.iutbm.dontwaste;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MealViewHolder extends RecyclerView.ViewHolder {

    public TextView mealNameView, userNameView, priceView;
    public ImageView pictureView;

    public MealViewHolder(View itemView){
        super(itemView);
        mealNameView = (TextView)itemView.findViewById(R.id.text_view_meal_name);
        userNameView = (TextView)itemView.findViewById(R.id.text_view_meal_userName);
        priceView = (TextView)itemView.findViewById(R.id.text_view_meal_price);

        pictureView = (ImageView)itemView.findViewById(R.id.image_view_meal_picture);
    }
}
