/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hari.autotasx;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import android.telephony.SmsManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import android.bluetooth.BluetoothAdapter;
import java.util.ArrayList;
import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "geofence-transitions-service";


    public GeofenceTransitionsIntentService() {

        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
           // Log.e(TAG, errorMessage);
            return;
        }


        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        int smsDb =0;
        int wifiDb=0;
        int silDb =0;
        int RemDb =0;
        int blueDb=0;
        int carDb =0;
        String remMsgDb = null;
        int RemDb_carPark =0;
        String remMsgDb_carPark =null;
        //Creating database object to fetch values
        ManageDB autodb = new ManageDB(this);
        autodb.open();

        if(!CarParkService.car_flag_static) {
            Cursor cursor = autodb.getEntry(ActionFragment.geoFence);
            cursor.moveToFirst();

            //System.out.println("cursor count"+cursor.getCount());

            //Log.i("logcat",cursor.getString(0)+", "+cursor.getString(1)+" , "+cursor.getString(2));
            smsDb = cursor.getInt(5);
            wifiDb = cursor.getInt(6);
            silDb = cursor.getInt(7);
            RemDb = cursor.getInt(8);
            remMsgDb = cursor.getString(9);
            blueDb = cursor.getInt(10);
            carDb = cursor.getInt(11);

            cursor.close();

        }
        else {
            Cursor cursor_carpark = autodb.getEntryCarPark(CarParkService.geofence_carpark);
            cursor_carpark.moveToFirst();

            carDb = cursor_carpark.getInt(11);
            remMsgDb_carPark = cursor_carpark.getString(9);

            cursor_carpark.close();
        }
        autodb.close();

        //Checking Enter/Exit transitions
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            System.out.println("smsdb"+smsDb);
                    if(RemDb==1){
            sendNotification(geofenceTransitionDetails,remMsgDb);}
                    if(carDb==1){
            sendNotification(geofenceTransitionDetails,remMsgDb_carPark);}
            //Perform actions if set on Actions set
            if(silDb==1)
            {
                Toast.makeText(this,"silent",Toast.LENGTH_SHORT).show();
                AudioManager am= (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

            }

            if(wifiDb==1){
                WifiManager wifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
            }

            if(blueDb==1){
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }
            }
            if(smsDb==1) {
                Toast.makeText(this, "inside sendsms()", Toast.LENGTH_SHORT).show();
                SmsManager sm = SmsManager.getDefault();
                System.out.printf("sm", sm);
                sm.sendTextMessage(ActionFragment.smsNo, null, "Hi, I am in " + ActionFragment.geoFence.getNameLoc() + " now!!!!", null, null);
            }
            if(carDb==1){
                 Intent mapIntent = new Intent(this, CarParkMap.class);
                 mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            if(silDb==1) {
                Toast.makeText(this,"general",Toast.LENGTH_SHORT).show();
                AudioManager am1= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                am1.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }

            if (wifiDb==1)
            {
                WifiManager wifiManager1 = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager1.setWifiEnabled(false);
            }
            if(blueDb==1){
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }
            }
            if(smsDb==1){
                Toast.makeText(this, "inside sendsms()", Toast.LENGTH_SHORT).show();
                SmsManager sm  = SmsManager.getDefault();
                System.out.printf("smsNo :"+ActionFragment.smsNo);
                //fetch
                sm.sendTextMessage(ActionFragment.smsNo, null, "Hi, I am outside "+ActionFragment.geoFence.getNameLoc()+" now !!!", null, null);
            }
            if(RemDb==1){
                sendNotification(geofenceTransitionDetails,remMsgDb);
            }

        }

    }


    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);


        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    private void sendNotification(String notificationDetails,String remMsgDb) {
        Toast.makeText(this,"here",Toast.LENGTH_SHORT).show();
        Intent notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);


        stackBuilder.addParentStack(MapsActivity.class);


        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


       builder.setSmallIcon(R.drawable.ic_add)

               .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_add))
                .setColor(Color.RED)
                .setContentTitle(remMsgDb)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(0, builder.build());

    }


    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
