package com.hari.autotasx;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CarParkService extends Service implements ConnectionCallbacks,OnConnectionFailedListener, ResultCallback<Status> {
    public CarParkService() {
    }
    protected Geofence mGeofence;
    protected ArrayList<Geofence> mGeofenceList ;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;
    private double lat;
    private double lon;
    public static GeoFence geofence_carpark;
    public static boolean car_flag_static = false;

    @Override
    public void onConnected(Bundle bundle) {
        car_flag_static = true;
        System.out.println("on connection...");
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                CarParkService.this.lat = mLastLocation.getLatitude();
                CarParkService.this.lon = mLastLocation.getLongitude();

                LatLng point = new LatLng(lat,lon);
                System.out.println("Point inside "+point);
                geofence_carpark = new GeoFence();
                geofence_carpark.setNameLoc("CarParking");
                geofence_carpark.setRadius((float) 100);
                geofence_carpark.setPoint(point);
                geofence_carpark.setRemmsg("You car is in and around 100 meters distance");


//                Toast.makeText(getBaseContext(), "adding to database", Toast.LENGTH_LONG).show();

                ManageDB dbinstance = new ManageDB(getBaseContext());
                dbinstance.open();
                dbinstance.addCarPark(geofence_carpark);
                //Adding to the geoservice
                mGeofenceList = new ArrayList<Geofence>();
                mGeofenceList.add(new Geofence.Builder()

                        .setRequestId("CarParking")

                        .setCircularRegion(
                                geofence_carpark.getPoint().latitude,
                                geofence_carpark.getPoint().longitude,
                                geofence_carpark.getRadius()
                        )

                        .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)

                        .build());

                try {
                    LocationServices.GeofencingApi.addGeofences(
                            mGoogleApiClient,

                            getGeofencingRequest(),

                            getGeofencePendingIntent()
                    ).setResultCallback(this);
                } catch (SecurityException securityException) {

                    Log.v("Error", securityException.toString());
                }

            }
        }catch (SecurityException securityException) {

            Log.v("Error", securityException.toString());
        }
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            mGoogleApiClient.connect();
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        buildGoogleApiClient();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Fetching current location", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Car Location saved!! Your good to go!", Toast.LENGTH_SHORT).show();
    }

}
