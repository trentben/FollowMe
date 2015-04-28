package com.teamawesome.followme;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.LocationSource;
import com.teamawesome.followme.util.Friend;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompassFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompassFragment extends Fragment implements SensorEventListener, LocationSource.OnLocationChangedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCATION_SOURCE = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final double DEST_LAT = 32.988934;
    private static final double DEST_LONG = -96.771528;

    // TODO: Rename and change types of parameters
    private LocationSource mLocationSource;
    private String mParam2;

    private TextView mCompassTV;
    private TextView mMetersTV;
    private ImageView mCompassImage;
    private SensorManager mSensorManager;
    private Location mDestLocation;
    private Location mUserLocation;
    private MapsActivity mParent;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CompassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompassFragment newInstance() {
        CompassFragment fragment = new CompassFragment();
       /* Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    public CompassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        mCompassTV = (TextView) view.findViewById(R.id.compass_tv);
        mCompassImage = (ImageView) view.findViewById(R.id.compass_arrow_image_view);
        mMetersTV = (TextView) view.findViewById(R.id.dist_meters_tv);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        mDestLocation = new Location("");
        mDestLocation.setLatitude(DEST_LAT);
        mDestLocation.setLongitude(DEST_LONG);

        return view;
    }

    public void setMapsActivitySource(MapsActivity parent){
        mParent = parent;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);

        mMetersTV.setText(R.string.waiting_for_gps);

    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setLocationSource(LocationSource ls)
    {
        mLocationSource = ls;
        mLocationSource.activate(this);
    }


    /*
     * Compass Methods
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float degress = event.values[0];
        float destBaring = 0;

        if(mUserLocation == null)
        {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            mUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(mUserLocation != null && mParent != null)
        {
            mDestLocation = new Location("");
            mDestLocation.setLatitude(mParent.mFriend.latitude);
            mDestLocation.setLongitude(mParent.mFriend.longitude);
            int dist = (int) mUserLocation.distanceTo(mDestLocation);
            destBaring = mUserLocation.bearingTo(mDestLocation);
            mMetersTV.setText(dist+" m");
            mCompassTV.setText(""+mParent.mFriend.latitude + ", " + mParent.mFriend.longitude);

        }


        //mCompassTV.setText(""+(int)degress);
        mCompassImage.setRotation(360-degress + destBaring);




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
     * Location Methods
     */
    @Override
    public void onLocationChanged(Location location) {
        mUserLocation = location;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
