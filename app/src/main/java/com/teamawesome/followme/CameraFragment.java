package com.teamawesome.followme;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.teamawesome.followme.views.AugmentedView;
import com.teamawesome.followme.views.PreviewCameraView;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment implements SensorEventListener, AugmentedView.CompassSource {

    // Native camera.
    private Camera mCamera;

    // View to display the camera output.
    private PreviewCameraView mPreview;

    // Reference to the containing view.
    private View mCameraView;
    private SensorManager mSensorManager;
    private Location mUserLocation, mDestLocation;
    private AugmentedView mAugmentedView;
    private float mCompassDegress;

    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        safeCameraOpenInView(view);
        mAugmentedView = (AugmentedView) view.findViewById(R.id.augmented_view);
        mAugmentedView.setCompassSource(this);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);


        return view;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        // for the system's orientation sensor registered listeners

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);


    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Recommended "safe" way to open the camera.
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        mCameraView = view;
        qOpened = (mCamera != null);

        if(qOpened == true){
            mPreview = new PreviewCameraView(getActivity().getBaseContext(), mCamera);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_frame);
            preview.addView(mPreview);
            //mPreview.startCameraPreview();
        }
        return qOpened;
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    /**
     * Safe method for getting a camera instance.
     * @return
     */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float degress = event.values[0];
        float destBaring = 0;

        if(mUserLocation != null)
        {
            int dist = (int) mUserLocation.distanceTo(mDestLocation);
            destBaring = mUserLocation.bearingTo(mDestLocation);
        }


        mCompassDegress = degress;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public float getCompassDegress() {
        return mCompassDegress;
    }
}
