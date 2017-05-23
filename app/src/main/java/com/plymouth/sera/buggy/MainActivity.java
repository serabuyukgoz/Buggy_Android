package com.plymouth.sera.buggy;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//for accelerometer sensor
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//test
import android.util.Log;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

//for warning message
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //Define variables
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;

    private static Button buttonD, buttonN, buttonR;

    float ay;
    boolean flagRight = true, flagLeft = true, flagN = true, nFlag;
    String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //settings for sensor variables
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //to create listener for warning message
        Listener warningServer = new Listener("10.188.110.84", 2005);
        warningServer.execute();

        //Streaming webcam view
        //Getting the streaming video from webserver and showing with webview
        String piAdd = "http://10.188.110.84:8081/"; //Set the Ip adress Manually
        WebView webView = (WebView) findViewById(R.id.videoStreamView);
        webView.loadUrl(piAdd);

        buttonListener();
    }


    //classes for SensorEventListener

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
    //Sensor Listener
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        // Accelerometer invoke when sensor changed
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ay = event.values[1];

            //go to function to eliminate sending same data when continuous on same direction
            rightOrLeft(ay);
        }
    }

   //this function invoke subscriber when direction changed
   public void rightOrLeft(float data){

       //assign right or left when forward or backward selected
       if (nFlag == false) {
           //if phone bend to right
           if (data > 1.5) {
               //turn right
               if (flagRight == true) {
                   flagN = true;
                   flagLeft = true;
                   flagRight = false;

                   send2Pi(response.concat("right"));
               }
             //if phone bend to left
           } else if (data < -1.5) {

               //turn left
               if (flagLeft == true) {
                   flagN = true;
                   flagLeft = false;
                   flagRight = true;

                   send2Pi(response.concat("left"));

               }
              //ıf phone not bended move directly
           } else {

               if (flagN == true) {
                   flagN = false;
                   flagLeft = true;
                   flagRight = true;

                   send2Pi(response);
               }
           }
       }
   }

    public void buttonListener()
    {
        buttonD = (Button) findViewById(R.id.dToggleButton);
        buttonN = (Button) findViewById(R.id.nToggleButton);
        buttonR = (Button) findViewById(R.id.rToggleButton);

        buttonD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                nFlag = false;

                buttonD.setBackgroundColor(Color.GREEN);
                buttonN.setBackgroundColor(Color.GRAY);
                buttonR.setBackgroundColor(Color.GRAY);

                response = "front";
                send2Pi(response);
            }
        });

        buttonN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                nFlag = true;

                buttonD.setBackgroundColor(Color.GRAY);
                buttonN.setBackgroundColor(Color.GREEN);
                buttonR.setBackgroundColor(Color.GRAY);

                response = "no";
                send2Pi(response);
            }
        });

        buttonR.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                nFlag = false;

                buttonD.setBackgroundColor(Color.GRAY);
                buttonN.setBackgroundColor(Color.GRAY);
                buttonR.setBackgroundColor(Color.GREEN);

                response = "back";
                send2Pi(response);
            }
        });
    }

    //sending data to client which will create a connection
    public void send2Pi (String data2send)
    {
        Client directionConnection = new Client(data2send);
        directionConnection.execute();
    }
}