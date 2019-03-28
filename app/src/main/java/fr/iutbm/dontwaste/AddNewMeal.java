package fr.iutbm.dontwaste;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewMeal extends AppCompatActivity {

    private static final int CAM_PERMISSION = 1102;
    private static final String PHOTO_FOLDER = "DontWaste";

    String outputFilePath;

    private SharedPreferences sharedPref;
    private EditText editTextMealName;
    private EditText editTextPrice;
    private TextView textViewEmail;
    private TextView textViewLatLong;
    private Button takePictureButton;
    private Button postButton;
    private ImageView imageViewMeal;

    private MealDAO mealDAO;

    Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_meal);

        requestPermissions();

        textViewEmail = findViewById(R.id.email);
        textViewLatLong = findViewById(R.id.lat_long);
        editTextMealName = findViewById(R.id.edit_text_name);
        editTextPrice = findViewById(R.id.edit_text_price);
        imageViewMeal = findViewById(R.id.image_view_add_meal);

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

        takePictureButton = findViewById(R.id.take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noPermissionToPost()) {
                    finish();
                    return;
                }
                goToCamera();
            }
        });

        postButton = findViewById(R.id.button_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noPermissionToPost()) {
                    finish();
                    return;
                }
                post();
            }
        });
    }

    private void goToCamera() {
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentTakePicture.resolveActivity(getPackageManager()) != null) {
            File imageFile = createImageFile();
            Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            outputFilePath = imageFile.getAbsolutePath();
            intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentTakePicture, CAM_PERMISSION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CAM_PERMISSION)
            try {
                onImageCaptureResult();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomFichierImage = "DONTWASTE_" + timeStamp;
        File dossierStockage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), PHOTO_FOLDER + "/");
        if (!dossierStockage.exists())
            dossierStockage.mkdir();
        return new File(dossierStockage, nomFichierImage + ".jpg");
    }

    private void onImageCaptureResult() throws IOException {
        if (outputFilePath != null) {
            File file = new File(outputFilePath);
            File publicFile = copyImageFile(file);
            Uri uriFinal = Uri.fromFile(publicFile);
            galleryAddPic(uriFinal);
            imageViewMeal.setImageURI(uriFinal);
            pictureUri = uriFinal;
        }
    }

    public File copyImageFile(File fichierACopier) throws IOException {
        File storageFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PHOTO_FOLDER + "/");
        if (!storageFolder.exists())
            storageFolder.mkdir();

        File copiedFile = new File(storageFolder, fichierACopier.getName());
        copiedFile.createNewFile();
        copy(fichierACopier, copiedFile);
        return copiedFile;
    }

    public static void copy(File source, File destination) throws IOException {
        InputStream inputStream = new FileInputStream(source);
        OutputStream outputStream = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    private void galleryAddPic(Uri uriContenu) {
        Intent scanContentIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriContenu);
        sendBroadcast(scanContentIntent);
    }

    public boolean noPermissionToPost() {
        if (ActivityCompat.checkSelfPermission(AddNewMeal.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to activate the Camera and Storage permissions in order to store your image and post a new meal ad !", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void post() {
        String mealName = editTextMealName.getText().toString();
        float mealPrice =  Float.parseFloat(editTextPrice.getText().toString());

        String email = sharedPref.getString("key_user_email", "email");
        float latitude = sharedPref.getFloat("key_latitude", 0.0f);
        float longitude = sharedPref.getFloat("key_longitude", 0.0f);

        Meal meal = new Meal(mealName, pictureUri.getPath(), mealPrice, email, latitude, longitude);

        mealDAO = ((AppDatabase.getDatabase(this)).mealDAO());
        (new InsertAsyncTask(mealDAO)).execute(meal);
        Toast.makeText(this, "Meal successfully added !", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 42);
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
