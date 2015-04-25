package com.teamawesome.followme.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Trent on 4/25/15.
 */
public class LocationBroadcasterService extends Service implements LocationListener {
    static LocationManager mLocationManager;

    @Override
    public void onCreate() {
        super.onCreate();

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


    }


    public void broadcastLocation(Location location) {
        Log.d("BROADCAST_SERVICE", "Lat:"+location.getLatitude()+" Long:"+location.getLongitude());
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
        mLocationManager.removeUpdates(this);
        Log.d("BROADCAST_SERVICE", "Service destroyed");
    }
}
