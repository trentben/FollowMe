/**
 * MapsActivity
 * created by Trent and Alex
 * This activity is responsible for showing the user a map with their location and there friends
 * location. It also allows the user to switch to a compass or camera mode.
 */
package com.teamawesome.followme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamawesome.followme.util.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MapsActivity extends ActionBarActivity implements LocationListener, LocationSource {

    public static final String FRIEND = "friend";
    private static final String MAP_FRAGMENT_TAG = "map";
    private static final String COMPASS_FRAGMENT_TAG = "compass";
    private static final String CAMERA_FRAGMENT_TAG = "camera";
    private static final String SAVED_FRAGMENT_STATE = "saved frag state";
    private static final Double DEFAULT_LAT = 32.7767;
    private static final Double DEFAULT_LONG = -96.7970;

    private String mCurrentFragment;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private CompassFragment mCompassFragment;
    private CameraFragment mCameraFragment;
    private LocationManager mLocationManager;
    public OnLocationChangedListener myLocationListener = null;
    public Friend mFriend;
    private LatLng mUserLocation;
    private FriendLocationUpdaterTask mFriendLocationUpdaterTask;
    private Marker mFriendMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle fromIntent = this.getIntent().getExtras();

        mFriend = (Friend) fromIntent.getSerializable(FRIEND);

        if(savedInstanceState != null)
        {
            String frag = savedInstanceState.getString(SAVED_FRAGMENT_STATE);
            showFragment(frag);
        }
        else
        {
            showFragment(COMPASS_FRAGMENT_TAG);
        }
    }


    @Override
    public void onResume(){
        super.onPause();

        if(mLocationManager == null)
        {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        //Set update listener
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        mFriendLocationUpdaterTask = new FriendLocationUpdaterTask(this);
        mFriendLocationUpdaterTask.execute("http://followme.byethost31.com/getusers.php?user=1&format=json");


    }

    @Override
    public void onPause(){
        super.onPause();
        mLocationManager.removeUpdates(this);
        mFriendLocationUpdaterTask.cancel(true);
        mFriendLocationUpdaterTask = null;
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
        switch (id) {
            case R.id.action_map:
                showFragment(MAP_FRAGMENT_TAG);
                return true;
            case R.id.action_compass:
                showFragment(COMPASS_FRAGMENT_TAG);
                return true;
            case R.id.action_camera:
                showFragment(CAMERA_FRAGMENT_TAG);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showCompassFragment(){
        if(mCompassFragment == null)
            mCompassFragment = CompassFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mCompassFragment).commit();
        mCompassFragment.setLocationSource(this);
        mCompassFragment.setMapsActivitySource(this);
    }

    public void showCameraFragment(){
        if(mCameraFragment == null)
            mCameraFragment = CameraFragment.newInstance();

        mCameraFragment.setMapsActivitySource(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mCameraFragment).commit();
        //mCameraFragment.setLocationSource(this);
    }

    public void showMapFragment(){
        // Creates initial configuration for the map
        GoogleMapOptions options = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(new LatLng(DEFAULT_LAT, DEFAULT_LONG), 16))
                .compassEnabled(false).mapType(GoogleMap.MAP_TYPE_NORMAL).rotateGesturesEnabled(true).scrollGesturesEnabled(true).tiltGesturesEnabled(false)
                .zoomControlsEnabled(true).zoomGesturesEnabled(true);

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
            fragmentTransaction.replace(R.id.map_container, mMapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }

        setUpMapIfNeeded();

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

        //get the last known Location and place a marker on the screen
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastKnownLocation != null)
        {
            mUserLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, 15));
        }

    }

    public void showFragment(String showFragment){
        switch(showFragment){
            case MAP_FRAGMENT_TAG:
                showMapFragment();
                setUpMapIfNeeded();
                break;
            case COMPASS_FRAGMENT_TAG:
                showCompassFragment();
                break;
            case CAMERA_FRAGMENT_TAG:
                showCameraFragment();
        }
        mCurrentFragment = showFragment;
    }

    /*
     * Location Services Callbacks
     */
   @Override
    public void onLocationChanged(Location location) {

       if (myLocationListener != null) {

           myLocationListener.onLocationChanged(location);

       }

       if (mMap == null && mMapFragment != null) {
           // Try to obtain the map from the SupportMapFragment.
           mMap = mMapFragment.getMap();
           // Check if we were successful in obtaining the map.
           if (mMap != null) {
               setUpMap();

           }
       }

       // my code to automatically set map to be in Dallas
             if(mMap!=null) {

                LatLng friend = new LatLng(mFriend.latitude, mFriend.longitude);

                 if(mFriendMarker != null)
                 {
                     mFriendMarker.remove();
                 }

                 mFriendMarker = mMap.addMarker(new MarkerOptions()
                         .title(mFriend.username)

                         .position(friend)
                         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(SAVED_FRAGMENT_STATE, mCurrentFragment);

        super.onSaveInstanceState(savedInstanceState);
    }




    private class FriendLocationUpdaterTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;


        public FriendLocationUpdaterTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrls) {
            String sUrl = sUrls[0];
            ArrayList<Friend> mFriendsList;
            String fileName = "friends.json";

            while(!isCancelled()) {
                //Download JSON file
                File outputFile = downloadHelper(sUrl);

                //If the outputFile returned is null, then we'll check the cache for a previous version
                if (outputFile == null) {
                    File casheFile = new File(context.getCacheDir().getAbsolutePath() + File.separator + fileName);

                    if (!casheFile.exists())
                        return "Could not get JSON file";

                    outputFile = casheFile;
                }

                //Parse JSON Data
                try {
                    mFriendsList = new ArrayList<>();

                    Scanner jsonInput = new Scanner(outputFile);
                    StringBuilder sBuilder = new StringBuilder();

                    while (jsonInput.hasNextLine())
                        sBuilder.append(jsonInput.nextLine());

                    String jsonStr = sBuilder.toString();

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray posts = jsonObj.getJSONArray("posts");

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject s = posts.getJSONObject(i);
                        JSONObject p = s.getJSONObject("post");


                        Friend friend = new Friend("");

                        friend.username = p.getString("username");
                        friend.latitude = p.getDouble("latitude");
                        friend.longitude = p.getDouble("longitude");

                        //downloadHelper(store.storeLogoURL);

                        mFriendsList.add(friend);

                    }

                    int fidx = mFriendsList.indexOf(mFriend);
                    if (fidx != -1) {
                        Friend f = mFriendsList.get(fidx);
                        mFriend.latitude = f.latitude;
                        mFriend.longitude = f.longitude;
                    }


                } catch (FileNotFoundException | JSONException e) {
                    e.printStackTrace();
                }

                //Wait 10 Seconds before reupdating location
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            // mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            /*mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);*/
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            //mProgressDialog.dismiss();
            if (result != null) {
                //mListener.onStoreListFragInteraction(StoreListFragment.MSG_DOWNLOAD_FAILED);
            }
            else {

            }
        }


        private File downloadHelper(String sUrl)
        {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String fileName = "friends.json";
            File outputFile = new File(context.getCacheDir().getAbsolutePath() + File.separator + fileName);
            try {
                URL url = new URL(sUrl);
                connection = (HttpURLConnection) url.openConnection();

                //Set the connection timeout to 5 sec
                connection.setConnectTimeout(5000);
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DOWNLOAD", "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return null;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();


                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();

            }

            return outputFile;
        }

    }

}
