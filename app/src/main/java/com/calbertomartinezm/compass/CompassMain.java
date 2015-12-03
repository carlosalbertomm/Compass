package com.calbertomartinezm.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassMain extends Activity implements SensorEventListener {

    private ImageView imgViewCompass;
    private TextView txtHeading;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagenetometer;

    private float currentDegree  = 0f;
    private float[] mGravity;
    private float[] mGeomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_main);

        imgViewCompass = (ImageView)findViewById(R.id.imgViewAguja);
        txtHeading = (TextView)findViewById(R.id.txtHeading);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagenetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagenetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mGeomagnetic = event.values;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuthInRadians = orientation[0];
                float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                azimuthInDegress = (float) (Math.rint(azimuthInDegress*100)/100);

                RotateAnimation rotateAnimation = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegress,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);
                rotateAnimation.setDuration(210);
                rotateAnimation.setFillAfter(true);
                imgViewCompass.startAnimation(rotateAnimation);
                currentDegree = -azimuthInDegress;

                Float fDegrees = Float.valueOf(Math.round(Math.abs(currentDegree)));

                txtHeading.setText(Float.toString(fDegrees) + "º");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}