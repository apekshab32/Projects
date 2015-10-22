package com.hari.autotasx;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class CarParkMap extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //protected List<String> mDataset;
    protected String db_lat;
    protected String db_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_park_map);

        System.out.println("Inside map activity");
        //mDataset = new ArrayList<String>();
        ManageDB dbinstance = new ManageDB(this);
        dbinstance.open();
        Cursor cursor_carpark = dbinstance.getCarParkProfile("CarParking");
        cursor_carpark.moveToFirst();

        for(int i=0;i<cursor_carpark.getCount();i++){
            db_lat = (cursor_carpark.getString(2));
            db_long = (cursor_carpark.getString(3));
            cursor_carpark.moveToNext();
        }
        System.out.println(db_lat);
        System.out.println(db_long);
        //String uri_string = "geo:"+ db_lat + "," + db_long +"z=30";
        //String uri_street = "geo:"+ db_lat + "," + db_long + "?q=my+street+address";
//        Uri uri = Uri.parse(uri_string);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//        startActivity(intent);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            if (db_lat != null){
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(db_lat), Double.parseDouble(db_long)), 18.0f));
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMap();
                }
            }
            else{
                mMap = null;
                Toast.makeText(this, "No Saved Park Location found" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(db_lat), Double.parseDouble(db_long))).title("Marker"));
    }
}
