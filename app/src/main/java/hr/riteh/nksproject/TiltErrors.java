package hr.riteh.nksproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class TiltErrors extends AppCompatActivity {

    ImageButton timeB;
    TiltErrorsView tiltErrorsView;
    Handler taskHandler;
    int task=0;
    int errorNumber=0;
    int defaultOrientation=0;
    int newOrientation=0;
    int FRAME_RATE = 20;
    int radius;
    private static final int NUMBER_OF_TASKS = 3;
    boolean taskCompleted=true;
    boolean setFirstOrientation=false;
    OrientationEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_errors);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics myDisplaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDisplaymetrics);
        final int screenHeight = myDisplaymetrics.heightPixels;
        final int screenWidth = myDisplaymetrics.widthPixels;
        LinearLayout mainView = findViewById(R.id.viewTE);
        timeB = findViewById(R.id.timeTE);
        radius = screenHeight / 36;

        tiltErrorsView = new TiltErrorsView(this);
        tiltErrorsView.setBallRadius((float) radius);
        tiltErrorsView.setCenterPosition(screenWidth/2,screenHeight/2);
        tiltErrorsView.setBallPosition(screenWidth/2-radius,screenHeight-4*radius);
        tiltErrorsView.setTargets();
        tiltErrorsView.setTargetPosition();
        mainView.addView(tiltErrorsView);


        timeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstOrientation=true;
                taskCompleted=false;
                tiltErrorsView.resetSpeed();
                taskHandler.postDelayed(myTask, FRAME_RATE);
                timeB.setVisibility(View.INVISIBLE);
            }
        });

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
               if(setFirstOrientation){

                   setFirstOrientation=false;
                   defaultOrientation=orientation;
                   Log.d("asd","ode sam****"+defaultOrientation);
               }

               newOrientation=orientation;
                Log.d("asd","newOriantation sam****"+newOrientation);
            }
        };
        if (mOrientationListener.canDetectOrientation() == true) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }


        tiltErrorsView.setBallInWallListener(new TiltErrorsView.ballInWallListener(){
            @Override
            public void onBallInWall(String input) {

                if(input.equals("wall")){
                    errorNumber++;
                    taskCompleted=true;
                    tiltErrorsView.setBallPosition(screenWidth/2-radius,screenHeight-4*radius);
                    tiltErrorsView.nextTarget();
                    timeB.setVisibility(View.VISIBLE);
                    taskDone();

                }

                if (input.equals("inside")) {

                    taskCompleted=true;
                    tiltErrorsView.setBallPosition(screenWidth/2-radius,screenHeight-4*radius);
                    tiltErrorsView.nextTarget();
                    timeB.setVisibility(View.VISIBLE);
                    taskDone();

                }
            }
        });
        taskHandler = new Handler();
    }

    public void taskDone(){
        task++;

        if(task == NUMBER_OF_TASKS){
            testingDone();
        }
    }

    private void testingDone() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TiltErrors.this);
        String userName = preferences.getString("username","");
        String results = (userName + " " + "TiltErrors "+errorNumber);


        try {
            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File myFile = new File(sdCard, "results.txt");

            if (! myFile.exists()) myFile.createNewFile();

            FileWriter fWriter = new FileWriter(myFile,true);
            fWriter.append(results + "\n");
            fWriter.close();
            Toast.makeText(TiltErrors.this, "Result successfully stored!", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
            Log.e("asd", "Could not write file " + e.getMessage());
        }

        Intent intent = new Intent(TiltErrors.this,MainActivity.class );
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        taskHandler.removeCallbacks(myTask);
        mOrientationListener.disable();
        this.finishAffinity();
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();

        if ((taskHandler!=null) && (myTask!=null))
            taskHandler.removeCallbacks(myTask);
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((taskHandler!=null) && (myTask!=null)) {
            taskHandler.postDelayed(myTask, FRAME_RATE);
        }
    }

    private Runnable myTask = new Runnable(){
        @Override
        public void run(){

            tiltErrorsView.moveBall(defaultOrientation,newOrientation);
            tiltErrorsView.setTargetPosition();
            tiltErrorsView.invalidate();

            if (!taskCompleted) {
                taskHandler.postDelayed(myTask, FRAME_RATE);
            }else{
                taskHandler.removeCallbacks(myTask);
            }
        }
    };
}