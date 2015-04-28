package com.teamawesome.followme.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.maps.LocationSource;
import com.teamawesome.followme.MapsActivity;

/**
 * Created by Trent on 4/21/15.
 * this view show an augmented compass
 */
public class AugmentedView extends SurfaceView implements SurfaceHolder.Callback, LocationSource.OnLocationChangedListener{

    private static final int DEG_WIDTH = 45;

    private Context mContext;
    private SurfaceHolder mHolder;
    private CompassSource mCompassSource;
    private Paint mRedPaint;
    private float mCompassBarring;
    private MyThread mThread;
    private MapsActivity mParent;
    private Location mUserLocation, mDestLocation;
    private float mDotSize;

    public AugmentedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        setZOrderOnTop(true);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        Resources r = getResources();
        mDotSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, r.getDisplayMetrics());

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);
        mRedPaint.setTextSize(textSize);
        mRedPaint.setTextAlign(Paint.Align.CENTER);




    }

    public void setMapsActivitySource(MapsActivity parent){
        mParent = parent;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new MyThread(holder, mContext,this);
        mThread.setRunning(true);
        mThread.start();

    }

    public void setCompassSource(CompassSource cs)
    {
        mCompassSource = cs;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCompassSource = null;

        mThread.setRunning(false);
        boolean retry = true;

        while(retry)
        {
            try
            {
                mThread.join();
                retry = false;

            }

            catch(Exception e)
            {
                Log.v("Exception Occured", e.getMessage());
            }

        }
    }


    private void doDraw(Canvas canvas){
        if(mCompassSource != null)
            mCompassBarring = mCompassSource.getCompassDegress();

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int degX = w/DEG_WIDTH;

        float destBaring = 0;

        if(mUserLocation == null)
        {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            mUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(mUserLocation != null && mParent != null)
        {
            mDestLocation = new Location("");
            mDestLocation.setLatitude(mParent.mFriend.latitude);
            mDestLocation.setLongitude(mParent.mFriend.longitude);
            int dist = (int) mUserLocation.distanceTo(mDestLocation);
            destBaring = mUserLocation.bearingTo(mDestLocation);

            int x = (int)(360 - mCompassBarring + destBaring + (DEG_WIDTH/2)) % 360;

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawCircle(x*degX, h/2, mDotSize, mRedPaint);

            canvas.drawText(dist + " m", x*degX, h/2 + (mDotSize*3), mRedPaint);
        }



    }

    @Override
    public void onLocationChanged(Location location) {
        mUserLocation = location;
    }


    class MyThread extends Thread

    {

        boolean mRun;
        Canvas mcanvas;
        SurfaceHolder surfaceHolder;
        Context context;
        AugmentedView msurfacePanel;



        public MyThread(SurfaceHolder sholder, Context ctx, AugmentedView spanel)

        {

            surfaceHolder = sholder;
            context = ctx;
            mRun = false;
            msurfacePanel = spanel;
        }



        void setRunning(boolean bRun)
        {
            mRun = bRun;
        }



        @Override

        public void run()
        {
            super.run();

            while(mRun)
            {
                mcanvas = surfaceHolder.lockCanvas();

                if(mcanvas != null)
                {
                    msurfacePanel.doDraw(mcanvas);
                    surfaceHolder.unlockCanvasAndPost(mcanvas);
                }
            }
        }
    }



    public interface CompassSource{
        float getCompassDegress();
    }
}

