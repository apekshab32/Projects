package com.hari.autotasx;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapLongClickListener,ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleMap mMap; // Might be null if Google Play services is not available.
    final int RQS_GooglePlayServices = 1;
    private Button mAddGeofencesButton;
    protected static final String TAG = "creating-and-monitoring-geofences";
    protected static GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    public static PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
    private Button mRemoveGeofencesButton;
    EditText radius;

    SharedPreferences prefs;
    ManageDB autodb = new ManageDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))

                .getMap();
        prefs = this.getSharedPreferences(
                "com.hari.autotasx", getApplication().MODE_PRIVATE);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        //mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);
        radius =  (EditText) findViewById(R.id.editText1);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapLongClickListener(this);
        mGeofenceList = new ArrayList<Geofence>();

        mGeofencePendingIntent = null;
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        setButtonsEnabledState();
        buildGoogleApiClient();
        //Toast.makeText(this,"jvjahjvhwagajhwaa"+ActionFragment.geoFence,Toast.LENGTH_SHORT).show();


    }

    //Remove geofences button handler
    /*public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,

                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {

            logSecurityException(securityException);
        }
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
            Toast.makeText(getApplicationContext(),
                    "isGooglePlayServicesAvailable SUCCESS",
                    Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
    }

    //Getting latitude and longitude on map long click
    @Override
    public void onMapLongClick(LatLng point) {
        mMap.setOnMapLongClickListener(this);
        ActionFragment.geoFence.setPoint(point);
       // ActionFragment.geoFence.setRadius();
        //int flag=0;
            /*if(ActionFragment.geoFence.getRadius()==30.0){

                 Toast.makeText(this,"Please Enter A valid radius",Toast.LENGTH_SHORT).show();
                 flag=1;
             }*/
           // if((ActionFragment.geoFence.getRadius()!=30.0)||(flag==1)) {
                ActionFragment.geoFence.setRadius(Float.parseFloat(radius.getText().toString()));
                mMap.addMarker(new MarkerOptions().position(point).title(point.toString()));

                CircleOptions circleOptions = new CircleOptions()
                    .center(point)   //set center
                    .radius(ActionFragment.geoFence.getRadius())   //set radius in meters
                    .strokeColor(Color.BLACK)
                    .strokeWidth(2);

                mMap.addCircle(circleOptions);

                ManageDB autodb = new ManageDB(this);
                autodb.open();
                boolean updateStatus = autodb.updateEntry(ActionFragment.geoFence);
                Cursor cursor = autodb.getEntry(ActionFragment.geoFence);
                cursor.moveToFirst();
                autodb.close();

                populateGeofenceList();
            //}


    }



    public void onResult(Status status) {
        if (status.isSuccess()) {

            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.commit();
            setButtonsEnabledState();
        } else {

            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
        }
    }

    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
            mAddGeofencesButton.setEnabled(true);
           // mRemoveGeofencesButton.setEnabled(true);
        } else {
            mAddGeofencesButton.setEnabled(true);
            //mRemoveGeofencesButton.setEnabled(true );
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

       // Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {

       // Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    //populate geofences
    public void populateGeofenceList() {
        ManageDB autodb = new ManageDB(this);
        autodb.open();
        Cursor cursor=autodb.getEntry(ActionFragment.geoFence);
        cursor.moveToFirst();
        autodb.close();
            mGeofenceList.add(new Geofence.Builder()

                   .setRequestId(cursor.getString(1))

                    .setCircularRegion(
                            cursor.getFloat(2),
                            cursor.getFloat(3),
                            cursor.getInt(4)
                    )

                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    .build());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Add geofences button handler
    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name",ActionFragment.geoFence.getNameLoc().toString());
        editor.putFloat ("lat",(float)ActionFragment.geoFence.getPoint().latitude);
        editor.putFloat ("lng",(float)ActionFragment.geoFence.getPoint().longitude);
        editor.commit();
        //Intent intent = new Intent(this, SaveFragment.class);
        //startActivity(intent);

        //Creating database object to update values
       /* ManageDB autodb = new ManageDB(this);
        autodb.open();
        boolean updateStatus=autodb.updateEntry(ActionFragment.geoFence);
        autodb.close();*/

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,

                    getGeofencingRequest(),

                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {

            logSecurityException(securityException);
        }
        finish();

    }
    private void logSecurityException(SecurityException securityException) {
        //Log.e(TAG, "Invalid location permission. " +
        //        "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    protected PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this.getApplicationContext(), GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
