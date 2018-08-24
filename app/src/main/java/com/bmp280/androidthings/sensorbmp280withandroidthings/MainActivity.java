package com.bmp280.androidthings.sensorbmp280withandroidthings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.bmx280.Bmx280;

import java.io.IOException;

public class MainActivity extends Activity {
    private String TAG = "Android BMP280 Sensor test";
    private Bmx280 mBmx280;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"start");
        try {
            Log.d(TAG,"initial I2C");
            mBmx280 = new Bmx280(BoardDefaults.getI2CPort());
            Log.d(TAG,"initial configure");
            // Configure driver settings
            mBmx280.setTemperatureOversampling(Bmx280.OVERSAMPLING_1X);
            //wake up sensor
            Log.d(TAG,"initial mode");
            mBmx280.setMode(Bmx280.MODE_NORMAL);
            Log.d(TAG,"connect with sensor");
        } catch (IOException e) {
            Log.d(TAG,"failed initial");
        }
        if (mBmx280 != null) {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Log.d(TAG, "start thread");
                            Thread.sleep(1000);
                            float temperature = mBmx280.readTemperature();
                            Log.d(TAG, String.valueOf(temperature));
                        } catch (IOException e) {
                            // error reading temperature
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "disconnect with  sensor");
        if (mBmx280 != null) {
            try {
                mBmx280.setMode(Bmx280.MODE_SLEEP);
                mBmx280.close();
            } catch (IOException e) {
                // error closing sensor
            }
        }
    }
}
