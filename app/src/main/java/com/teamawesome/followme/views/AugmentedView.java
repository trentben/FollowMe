package com.teamawesome.followme.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Trent on 4/21/15.
 */
public class AugmentedView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Paint mRedPaint;

    public AugmentedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setZOrderOnTop(true);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = holder.lockCanvas();
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        canvas.drawCircle(w/2, h/2, 40f, mRedPaint);

        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
