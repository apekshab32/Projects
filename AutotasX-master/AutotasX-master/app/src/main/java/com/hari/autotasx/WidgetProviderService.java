package com.hari.autotasx;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by Meghana on 4/17/2015.
 */
public class WidgetProviderService extends AppWidgetProvider {

    public static String ADD_DB = "CarParkService";
    private static boolean serviceRunning = false;
    private static Intent serviceIntent;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        Log.i(ADD_DB, "inside onUpdate");
        serviceIntent = new Intent(context, CarParkService.class);

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            System.out.println("inside onUpdate function");


            Intent newIntent = new Intent(context, CarParkService.class);
            //newIntent.setAction(ADD_DB);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, newIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.park_button, pendingIntent);

            Intent mapIntent = new Intent(context, CarParkMap.class);
            //newIntent.setAction(ADD_DB);
            PendingIntent pendingMapIntent = PendingIntent.getActivity(context, 0, mapIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.find_button, pendingMapIntent);

//            Intent uri_intent = new Intent(android.content.Intent.ACTION_VIEW,
//                    Uri.parse("http://maps.google.com/maps?daddr=20.5666,45.345"));
//            startActivity(uri_intent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            //context.startService(newIntent);

        }
    }
    // for now not needed
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ADD_DB)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            // Create a fresh intent
            Intent serviceIntent = new Intent(context, CarParkService.class);

            if(serviceRunning) {
                context.stopService(serviceIntent);
//                remoteViews.setViewVisibility(R.id.buttonWidgetStartService, View.VISIBLE);
//                remoteViews.setViewVisibility(R.id.buttonWidgetStopService, View.INVISIBLE);
                Toast.makeText(context, "serviceStopped", Toast.LENGTH_LONG).show();
            } else {
                context.startService(serviceIntent);
//                remoteViews.setViewVisibility(R.id.buttonWidgetStopService, View.VISIBLE);
//                remoteViews.setViewVisibility(R.id.buttonWidgetStartService, View.INVISIBLE);
                Toast.makeText(context, "serviceStarted", Toast.LENGTH_LONG).show();
            }
            serviceRunning=!serviceRunning;
            ComponentName componentName = new ComponentName(context, CarParkService.class);
            AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
        }
        super.onReceive(context, intent);
    }
}
