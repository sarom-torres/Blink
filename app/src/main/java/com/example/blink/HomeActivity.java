package com.example.blink;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;

import com.google.android.things.pio.PeripheralManager; // deveria ser PeripheralManagerService

import java.io.IOException;

public class HomeActivity extends Activity {

    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    private static final String LED = "BCM6";


    private static final String TAG = "MainActivity";
    private Handler mHandler = new Handler();
    private Gpio mLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PeripheralManager manager = PeripheralManager.getInstance();

        try {
            mLedGpio = manager.openGpio(LED);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "Starting blinking LED GPIO pin");
            mHandler.post(mBlinkRunnable);

        }catch (IOException e){
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacks(mBlinkRunnable);
        Log.i(TAG,"Closing LED GPIO pin");

        try{
            mLedGpio.close();
        }catch (IOException e){
            Log.e(TAG, "Error on PeripheralIO API", e);
        }finally {
            mLedGpio = null;
        }

    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if(mLedGpio == null){
                return;
            }

            try{
                mLedGpio.setValue(!mLedGpio.getValue());
                Log.d(TAG, "State set to " + mLedGpio.getValue());
                mHandler.postDelayed(mBlinkRunnable,INTERVAL_BETWEEN_BLINKS_MS);

            }catch (IOException e){
                Log.e(TAG,"Error on PeripheralIO API",e);
            }
        }
    };


}
