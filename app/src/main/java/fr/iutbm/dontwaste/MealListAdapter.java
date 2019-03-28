package fr.iutbm.dontwaste;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MealListAdapter extends RecyclerView.Adapter<MealViewHolder> {

    private List<Meal> mealList;
    private Context context;

    public MealListAdapter(List<Meal> mealList, Context context) {
        this.mealList = mealList;
        this.context = context;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_meal, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.mealNameView.setText(meal.getMealName());
        holder.userNameView.setText("Added by " + meal.getUserName());
        holder.priceView.setText("Price: " + meal.getPrice() + " €");

        /*try {
            String imageName = meal.getBmppath();
            InputStream ims = context.getAssets().open(imageName);
            Drawable d = Drawable.createFromStream(ims, null);
            holder.pictureView.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex) {
            return;
        }*/

        holder.pictureView.setImageURI(Uri.parse(meal.getBmppath()));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }
}
