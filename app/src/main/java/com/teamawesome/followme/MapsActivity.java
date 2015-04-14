package com.teamawesome.followme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends ActionBarActivity implements LocationListener, LocationSource {

    private static final String MAP_FRAGMENT_TAG = "map";

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private CompassFragment mCompassFragment;
    private LocationManager mLocationManager;
    OnLocationChangedListener myLocationListener = null;
    private LatLng mUserLocation;

    private int tempFix = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        showMapFragment();
    }


    @Override
    public void onResume(){
        super.onPause();
        setUpMapIfNeeded();

        if(mLocationManager == null)
        {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        //Set update listener
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //get the last known Location and place a marker on the screen
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastKnownLocation != null)
        {
            mUserLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, 15));
        }


    }

    @Override
    public void onPause(){
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compass) {
            if(tempFix == 0) {
                closeMap();
                showCompassFragment();
                tempFix = 1;
            }
            else{
                closeCompass();
                showMapFragment();
                tempFix = 0;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMapFragment(){
        // Creates initial configuration for the map
        GoogleMapOptions options = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(new LatLng(37.4005502611301, -5.98233461380005), 16))
                .compassEnabled(false).mapType(GoogleMap.MAP_TYPE_NORMAL).rotateGesturesEnabled(false).scrollGesturesEnabled(false).tiltGesturesEnabled(false)
                .zoomControlsEnabled(false).zoomGesturesEnabled(false);

        // Modified from the sample code:
        // It isn't possible to set a fragment's id programmatically so we set a
        // tag instead and search for it using that.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // We only create a fragment if it doesn't already exist.
        if (mMapFragment == null) {
            // To programmatically add the map, we first create a
            // SupportMapFragment.
            mMapFragment = SupportMapFragment.newInstance(options);
            // Then we add it using a FragmentTransaction.
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.map_container, mMapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }

        setUpMapIfNeeded();
    }

    private void closeMap() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(mMapFragment);
        fragmentTransaction.commit();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setLocationSource(this);
    }

    public void showCompassFragment(){
        if(mCompassFragment == null)
            mCompassFragment = CompassFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.map_container, mCompassFragment).commit();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mCompassFragment);
    }

    public void closeCompass(){
        getSupportFragmentManager().beginTransaction().remove(mCompassFragment).commit();
    }


    /*
     * Location Services Callbacks
     */
   @Override
    public void onLocationChanged(Location location) {
       if (myLocationListener != null) {
           myLocationListener.onLocationChanged(location);
       }
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

    /*
 * LocationSource Methods
 */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        myLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }




}
