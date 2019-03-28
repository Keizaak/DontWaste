package fr.iutbm.dontwaste;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DirectionsAsyncTask extends AsyncTask<Object, String, String> {

    Context mContext;
    Location start;
    LatLng positionMarker;
    Location dest;
    GoogleMap mGoogleMap;
    String url;
    String directions = "";


    public DirectionsAsyncTask(Context mContext, Location start, LatLng positionMarker, GoogleMap mGoogleMap) {
        super();
        this.mContext = mContext;
        this.start = start;

        this.positionMarker = positionMarker;
        dest = new Location("");
        dest.setLatitude(positionMarker.latitude);
        dest.setLongitude(positionMarker.longitude);

        this.mGoogleMap = mGoogleMap;

    }

    @Override
    protected String doInBackground(Object... params) {
        directions = getDirections();
        return directions;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONArray jsonArray = interprete_json_file(directions);

            String[] polylines_array = new String[jsonArray.length()];
            JSONObject jsonObject2;

            for(int i = 0 ; i < jsonArray.length() ; i++){
                jsonObject2 = jsonArray.getJSONObject(i);
                String polygone = jsonObject2.getJSONObject("polyline").getString("points");
                polylines_array[i] = polygone;
            }

            for(int i = 0 ; i < polylines_array.length ; i++){
                PolylineOptions options = new PolylineOptions();
                options.color(Color.RED);
                options.width(10);
                options.addAll(PolyUtil.decode(polylines_array[i]));

                mGoogleMap.addPolyline(options);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String getDirections(){
        String r = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://maps.googleapis.com/maps/api/directions/json?");
            sb.append("&mode=walking");
            sb.append("&origin=" + start.getLatitude() + "," + start.getLongitude());
            sb.append(("&destination=" + dest.getLatitude()) + "," + dest.getLongitude());
            sb.append("&key=AIzaSyABbVhsR_4HOFTHgcq80LLC36mpQSIOX5Y");
            URL url = new URL(sb.toString());
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();
            r = convertStreamToString(conn.getInputStream());

        } catch( Exception e){
            e.printStackTrace();
        }
        return r;
    }

    private String convertStreamToString(InputStream in){
        String reponse = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            reponse= new String(response, "UTF-8");
        } catch (Exception e){
            e.printStackTrace();
        }
        return reponse;
    }

    private JSONArray interprete_json_file(String jsonText){
        JSONArray directions = null;
        try {
            JSONObject racine = new JSONObject(jsonText);
            JSONArray routes = racine.getJSONArray("routes");
            JSONObject ob1 = routes.getJSONObject(0);
            JSONArray legs = ob1.getJSONArray("legs");
            JSONObject ob2 = legs.getJSONObject(0);
            JSONArray steps = ob2.getJSONArray("steps");
            directions = steps;
        } catch(JSONException e){
            e.printStackTrace();
        }
        return directions;
    }

}
