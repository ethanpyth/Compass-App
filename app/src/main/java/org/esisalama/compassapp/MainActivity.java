package org.esisalama.compassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager compassSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] accel_read;
    private float[] magnetic_read;
    private TextView tv_degrees;
    private ImageView iv_compass;
    private float current_degree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    @Override
    protected void onResume(){
        super.onResume();
        compassSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause(){
        super.onPause();
       compassSensorManager.unregisterListener(this);
    }

    public void initComponents(){
        tv_degrees = findViewById(R.id.tv_degrees);
        iv_compass = findViewById(R.id.imageViewCompass);
        compassSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accel_read = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetic_read = event.values;
        if (accel_read != null && magnetic_read != null){
            float[] R = new float[9];
            float[] I = new float[9];
            boolean successful_read = SensorManager.getRotationMatrix(R, I, accel_read, magnetic_read);
            if (successful_read){
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth_angle = orientation[0];
                float degrees = ((azimuth_angle * 180f) / 3.14f);
                int degreesInt = Math.round(degrees);
                tv_degrees.setText(String.format("%s%s to absolute north.", degreesInt, (char) 0x00B0));
                RotateAnimation rotateAnimation = new RotateAnimation(
                        current_degree,
                        -degreesInt,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );
                iv_compass.startAnimation(rotateAnimation);
                current_degree = -degreesInt;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}