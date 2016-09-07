/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.decode;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.libs.zxing.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial transparency outside it, as well as the laser scanner animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View
{

    private static final int[]      SCANNER_ALPHA         =
                                                          { 0, 64, 128, 192, 255, 192, 128, 64 };
    private static final int        OPAQUE                = 0xFF;
    private static final long       ANIMATION_DELAY       = 50L;
    private static final int        CURRENT_POINT_OPACITY = 0xFF;
    private static final int        MAX_RESULT_POINTS     = 20;
    private static final int        POINT_SIZE            = 6;

    private CameraManager           cameraManager;
    private final Paint             paint;
    private Bitmap                  resultBitmap;
    private final int               maskColor;
    private final int               resultColor;
    private final int               laserColor;
    private final int               resultPointColor;
    private final int               frameColor;
    private int                     scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    
    private int margin = 0;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs)
    {
        super (context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint (Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources ();
        maskColor = resources.getColor (R.color.viewfinder_mask);
        resultColor = resources.getColor (R.color.result_view);
        laserColor = resources.getColor (R.color.viewfinder_laser);
        resultPointColor = resources.getColor (R.color.possible_result_points);
        frameColor = resources.getColor (R.color.viewfinder_frame);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<ResultPoint> (5);
        lastPossibleResultPoints = null;
    }

    public void setCameraManager(CameraManager cameraManager){
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas){
        if (cameraManager == null) { return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect ();
        if (frame == null) { return; }
        int width = canvas.getWidth ();
        int height = canvas.getHeight ();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor (resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect (0, 0, width, frame.top, paint);
        canvas.drawRect (0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect (frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect (0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null)
        {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha (CURRENT_POINT_OPACITY);
            canvas.drawBitmap (resultBitmap, null, frame, paint);
        } else
        {

            int linewidht = 10;
            paint.setColor (frameColor);

            canvas.drawRect (frame.left, frame.top,(linewidht + frame.left), (50 + frame.top), paint);
            canvas.drawRect (frame.left, frame.top,(50 + frame.left), (linewidht + frame.top), paint);
            canvas.drawRect (((0 - linewidht) + frame.right), frame.top, (1 + frame.right), (50 + frame.top), paint);
            canvas.drawRect ((-50 + frame.right), frame.top, frame.right, (linewidht + frame.top), paint);
            canvas.drawRect (frame.left, (-49 + frame.bottom), (linewidht + frame.left), (1 + frame.bottom), paint);
            canvas.drawRect (frame.left, ((0 - linewidht) + frame.bottom), (50 + frame.left), (1 + frame.bottom), paint);
            canvas.drawRect (((0 - linewidht) + frame.right), -1 + (-49 + frame.bottom), (1 + frame.right), (1 + frame.bottom), paint);
            canvas.drawRect ((-50 + frame.right), ((0 - linewidht) + frame.bottom), frame.right, (linewidht - (linewidht - 1) + frame.bottom), paint);
            
            paint.setAntiAlias(true);
            paint.setStrokeWidth(4);
            canvas.drawLine (frame.left, frame.top + margin, frame.right, frame.top + margin, paint);
            
            margin = (margin+4) % (frame.top);
            
            paint.setAlpha (SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int vmiddle = frame.height () / 2 + frame.top;
            int hmiddle = frame.width () / 2 + frame.left;
            canvas.drawRect (hmiddle - 20, vmiddle - 1, hmiddle + 20, vmiddle + 2, paint);
            canvas.drawRect (hmiddle - 1, vmiddle - 20, hmiddle + 2, vmiddle + 20, paint);
            postInvalidateDelayed (ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder(){
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null)
        {
            resultBitmap.recycle ();
        }
        invalidate ();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     * 
     * @param barcode
     *            An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode){
        resultBitmap = barcode;
        invalidate ();
    }

    public void addPossibleResultPoint(ResultPoint point){
        possibleResultPoints.add (point);
    }

}
