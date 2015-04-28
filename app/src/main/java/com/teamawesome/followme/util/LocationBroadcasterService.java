package com.teamawesome.followme.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.teamawesome.followme.HomeActivity;
import com.teamawesome.followme.LoginActivity;
import com.teamawesome.followme.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Trent on 4/25/15.
 */
public class LocationBroadcasterService extends Service implements LocationListener {
    private static boolean isBroadcasting = false;
    private static final int NOTIFICATION_ID = 151;
    static LocationManager mLocationManager;

    private String mUsername;

    @Override
    public void onCreate() {
        super.onCreate();

        //Get Username

        try {
            InputStream userNameFile = openFileInput(LoginActivity.USER_FILE);
            Scanner input = new Scanner(userNameFile);
            mUsername = input.nextLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);

        createNotification();

        isBroadcasting = true;

    }


    public void broadcastLocation(Location location) {
        Log.d("BROADCAST_SERVICE", "Lat:"+location.getLatitude()+" Long:"+location.getLongitude());
        PostLocationTask postLocationTask = new PostLocationTask();
        postLocationTask.execute("http://followme.byethost31.com/updatelocation.php?json={%22UserName%22:%22"+mUsername+"%22,%20%22Latitude%22:" + location.getLatitude() +",%20%22Longitude%22:" +location.getLongitude()+"}");
    }

    public void createNotification() {
        //when notification is clicked it takes user to HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.broadcast_active);
        builder.setOngoing(true);
        builder.setContentIntent(pIntent);
        builder.setAutoCancel(false);
        builder.setContentTitle("FollowMe");
        builder.setContentText("You are broadcasting your location");
        builder.setSubText("Click to open FollowMe");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onLocationChanged(Location location) {
        broadcastLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isBroadcasting = false;
        mLocationManager.removeUpdates(this);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        Log.d("BROADCAST_SERVICE", "Service destroyed");
    }

    public static boolean isBroadcasting(){
        return isBroadcasting;
    }

    private class PostLocationTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... urls) {
            String sUrl = urls[0];

            HttpURLConnection connection = null;
            URL url = null;
            try {
                url = new URL(sUrl);
                connection = (HttpURLConnection) url.openConnection();

                //Set the connection timeout to 5 sec
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "close");
                connection.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.setConnectTimeout(5000);
                connection.connect();
                connection.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
