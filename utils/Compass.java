package com.igenesys.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.igenesys.R;
//import com.techaidsolution.gdc_app.R;

public class Compass extends View {
    float mAngle = 0;

    Paint mPaint;

    Bitmap mBitmap;

    Matrix mMatrix;

    MapView mMapView;
    SceneView mMapSceneView;

    // Called when the Compass view is inflated from XML. In this case, no attributes are initialized from XML.
    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create a Paint, Matrix and Bitmap that will be re-used together to draw the
        // compass image each time the onDraw method is called.
        mPaint = new Paint();
        mMatrix = new Matrix();

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        int widthHeight=60;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                widthHeight=60;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                widthHeight=100;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                widthHeight=60;
                break;
            default:
                widthHeight=60;
        }
        AppLog.d(widthHeight+"::WidthHeight::"+screenSize);
        // Create the bitmap of the compass from a resource.
        mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_compasss), widthHeight, widthHeight, false);
    }

    /**
     * Overloaded constructor that takes a MapView, from which the compass rotation angle will be set.
     */
    public Compass(Context context, AttributeSet attrs, MapView mapView) {
        this(context, attrs);
    }

    /**
     * Updates the angle, in degrees, at which the compass is draw within this view.
     */
    public void setRotationAngle(double angle) {
        // Save the new rotation angle.
        mAngle = (float) angle;

        // Force the compass to re-paint itself.
        postInvalidate();
    }

    /**
     * Draws the compass image at the current angle of rotation on the canvas.
     */
    @Override
    protected void onDraw(Canvas canvas) {

        // Reset the matrix to default values.
        mMatrix.reset();

        // Pass the current rotation angle to the matrix. The center of rotation is set to be the center of the bitmap.
        mMatrix.postRotate(-this.mAngle, mBitmap.getHeight() / 2, mBitmap.getWidth() / 2);

        // Use the matrix to draw the bitmap image of the compass.
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        super.onDraw(canvas);

    }

    public void bindTo(MapView map, Compass ivCompass) {
        mMapView = map;
        if (mMapView != null) {
            // Set an OnPinchListener on the map to listen for the pinch gesture which may change the map rotation.
            mMapView.addViewpointChangedListener(viewpointChangedEvent ->
            {
                if (viewpointChangedEvent.getSource() instanceof MapView) {
                    double angle = ((MapView) viewpointChangedEvent.getSource()).getMapRotation();
                    ivCompass.setVisibility(VISIBLE);
                    setRotationAngle(angle);
                }

            });
        }
    }

    public void bindTo(SceneView map, Compass ivCompass) {
        mMapSceneView = map;
        if (mMapSceneView != null) {
            // Set an OnPinchListener on the map to listen for the pinch gesture which may change the map rotation.
            mMapSceneView.addViewpointChangedListener(viewpointChangedEvent ->
            {
                if (viewpointChangedEvent.getSource() instanceof SceneView) {
                    double angle = ((SceneView) viewpointChangedEvent.getSource()).getCurrentViewpointCamera().getHeading();
                    ivCompass.setVisibility(VISIBLE);
                    setRotationAngle(angle);
                }
            });
        }
    }
}
