package com.example.admin.breakout;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by admin on 2015/06/02.
 */
public class MoveListener implements SensorEventListener{
    private ArrayList<MyBall> balls;
    private float accelerationX;
    private float accelerationY;

    MoveListener(ArrayList balls){
        this.balls = balls;
    }


    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                accelerationX = event.values[SensorManager.DATA_X];
                accelerationY = event.values[SensorManager.DATA_Y];
                for(int i = 0;i<balls.size();i++){
                    balls.get(i).changeAcceleration(accelerationX,accelerationY);
                }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
