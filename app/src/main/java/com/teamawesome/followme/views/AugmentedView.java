package com.teamawesome.followme.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Trent on 4/21/15.
 */
public class AugmentedView extends SurfaceView implements SurfaceHolder.Callback{

    private static final int DEG_WIDTH = 70;

    private Context mContext;
    private SurfaceHolder mHolder;
    private CompassSource mCompassSource;
    private Paint mRedPaint;
    private float mCompassBarring;
    private MyThread mThread;

    public AugmentedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        setZOrderOnTop(true);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);

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

        int x = (int)(360 - mCompassBarring + 0 + (DEG_WIDTH/2)) % 360;

        Log.d("COM", ""+x);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawCircle(x*degX, h/2, 40f, mRedPaint);

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

