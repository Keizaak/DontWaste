package fr.iutbm.dontwaste;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private AppDatabase appDatabase;
    private MealDAO mealDAO;
    private List<Meal> mealList = new ArrayList<>();

    private GoogleApiClient mGoogleApiClient = null;
    private static int REQUEST_LOCATION = 1;
    private Location mLastLocation;

    private boolean mRequestLocationUpdates;
    private LocationRequest mLocationRequest;

    private GoogleMap mGoogleMap;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appDatabase = AppDatabase.getDatabase(getContext());
        mealDAO = appDatabase.mealDAO();
        (new GetAllMealsMapAsyncTask(mealDAO)).execute();

        View root = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume(){
        super.onResume();
        setLocationParameters();
        if(mGoogleApiClient.isConnected()){
            if(mRequestLocationUpdates){
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        }
    }

    protected void startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestLocationPermission();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.setOnMarkerClickListener(this);

        if(!mealList.isEmpty()){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(Meal meal : mealList){
                LatLng pos = new LatLng(meal.getLatitude(), meal.getLongitude());
                builder.include(pos);
                LatLngBounds bounds = builder.build();
                mGoogleMap.addMarker(new MarkerOptions().position(pos).title(meal.getMealName()).alpha(0.5f).snippet(meal.getPrice() + " €"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestLocationPermission();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            /*if(mLastLocation != null){
                LatLng userPos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(userPos).title("You").icon(BitmapDescriptorFactory.defaultMarker(150f)));
            }*/
        }

        if(mRequestLocationUpdates){
            startLocationUpdates();
        } else {
            stopLocationUpdates();
        }
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    public void setLocationParameters(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mRequestLocationUpdates = sharedPreferences.getBoolean(getResources().getString(R.string.key_location_switch), false);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long) Double.parseDouble(sharedPreferences.getString(getResources().getString(R.string.key_search_delay), "100")));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(location != null){
            double radius = Double.parseDouble(sharedPreferences.getString(getResources().getString(R.string.key_search_radius), "100"));

            mGoogleMap.clear();
            LatLngBounds.Builder builder = LatLngBounds.builder();

            LatLng userPos = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(userPos).title("You").icon(BitmapDescriptorFactory.defaultMarker(150f)));
            builder.include(userPos);

            // Save position for AddMeal
            sharedPreferences.edit().putFloat("key_latitude", (float) location.getLatitude()).apply();
            sharedPreferences.edit().putFloat("key_longitude", (float) location.getLongitude()).apply();

            for(Meal meal : mealList){
                LatLng pos = new LatLng(meal.getLatitude(), meal.getLongitude());
                builder.include(pos);

                MarkerOptions markerOptions = new MarkerOptions().position(pos).title(meal.getMealName()).alpha(0.5f).snippet(meal.getPrice() + " €");

                Location mealLocation = new Location("");
                mealLocation.setLatitude(meal.getLatitude());
                mealLocation.setLongitude(meal.getLongitude());

                if(location.distanceTo(mealLocation) < radius){
                    markerOptions.alpha(1);
                }

                mGoogleMap.addMarker(markerOptions);
            }

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class GetAllMealsMapAsyncTask extends AsyncTask<Void, Void, Void>{
        private MealDAO mAsyncTaskDao;
        ArrayList<Meal> meals;

        GetAllMealsMapAsyncTask(MealDAO dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids){
            meals = new ArrayList<>(mAsyncTaskDao.getAllMeals());
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            mealList = meals;
        }
    }
}
