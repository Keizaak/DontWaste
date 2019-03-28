package fr.iutbm.dontwaste;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewMeal extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private EditText editTextMealName;
    private EditText editTextPrice;
    private TextView textViewEmail;
    private TextView textViewLatLong;
    private Button postButton;

    private AppDatabase appdb;
    private MealDAO mealDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_meal);

        textViewEmail = findViewById(R.id.email);
        textViewLatLong = findViewById(R.id.lat_long);
        editTextMealName = findViewById(R.id.edit_text_name);
        editTextPrice = findViewById(R.id.edit_text_price);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String email = "ID : " + sharedPref.getString("key_user_email", "email");
        textViewEmail.setText(email);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Position - ");
        stringBuilder.append("Latitude : ");
        stringBuilder.append(sharedPref.getFloat("key_latitude", 0.0f));
        stringBuilder.append(", Longitude : ");
        stringBuilder.append(sharedPref.getFloat("key_longitude", 0.0f));
        textViewLatLong.setText(stringBuilder);

        postButton = findViewById(R.id.button_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private void post() {
        String mealName = editTextMealName.getText().toString();
        float mealPrice =  Float.parseFloat(editTextPrice.getText().toString());

        String email = sharedPref.getString("key_user_email", "email");
        float latitude = sharedPref.getFloat("key_latitude", 0.0f);
        float longitude = sharedPref.getFloat("key_longitude", 0.0f);

        Meal meal = new Meal(mealName, "carbonara.jpg", mealPrice, email, latitude, longitude);

        mealDAO = ((AppDatabase.getDatabase(this)).mealDAO());
        (new InsertAsyncTask(mealDAO)).execute(meal);
        Toast.makeText(this, "Meal successfully added !", Toast.LENGTH_SHORT).show();
        finish();
    }

    private class InsertAsyncTask extends AsyncTask<Meal, Void, Void> {

        private MealDAO dao;
        InsertAsyncTask(MealDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Meal... params) {
            for (Meal m : params) {
                this.dao.insertMeals(m);
            }
            return null;
        }
    }
}
