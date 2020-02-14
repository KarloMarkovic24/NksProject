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
import java.util.ArrayList;

public class ArrowErrors extends AppCompatActivity {

    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton timeB;
    ArrowErrorsView arrowErrorsView;
    Handler taskHandler;
    int task=0;
    int errorNumber=0;
    int flag=0;
    int FRAME_RATE = 20;
    int radius;
    private static final int NUMBER_OF_TASKS = 3;
    boolean taskCompleted=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_errors);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics myDisplaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDisplaymetrics);
        final int screenHeight = myDisplaymetrics.heightPixels;
        final int screenWidth = myDisplaymetrics.widthPixels;
        LinearLayout mainView = findViewById(R.id.viewAE);
        leftButton = findViewById(R.id.leftButtonAE);
        rightButton = findViewById(R.id.rightButtonAE);
        timeB = findViewById(R.id.timeAE);
        radius = screenHeight / 36;

        arrowErrorsView = new ArrowErrorsView(this);
        arrowErrorsView.setBallRadius((float) radius);
        arrowErrorsView.setCenterPosition(screenWidth/2,screenHeight/2);
        arrowErrorsView.setBallPosition(screenWidth/2-radius,screenHeight-4*radius);
        arrowErrorsView.setTargets();
        arrowErrorsView.setTargetPosition();
        mainView.addView(arrowErrorsView);


        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    flag=2;
                }else if (event.getAction() == MotionEvent.ACTION_UP) {
                    flag=3;
                }
                return true;
            }
        });

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    flag=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    flag=3;
                }
                return true;
            }
        });

        timeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskCompleted=false;
                taskHandler.postDelayed(myTask, FRAME_RATE);
                timeB.setVisibility(View.INVISIBLE);
            }
        });

        arrowErrorsView.setBallInWallListener(new ArrowErrorsView.ballInWallListener() {
            @Override
            public void onBallInWall(String input) {

                if(input.equals("wall")){
                    errorNumber++;
                    taskCompleted=true;
                    arrowErrorsView.setBallPosition(screenWidth/2-radius,screenHeight-4*radius);
                    arrowErrorsView.nextTarget();
                    timeB.setVisibility(View.VISIBLE);
                    taskDone();

                }

                if (input.equals("inside")) {

                        taskCompleted=true;
                        arrowErrorsView.setBallPosition(screenWidth/2-radius,screenHeight-4*radius);
                        arrowErrorsView.nextTarget();
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ArrowErrors.this);
        String userName = preferences.getString("username","");
        String results = (userName + " " + "ArrowErrors "+errorNumber);


        try {
            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File myFile = new File(sdCard, "results.txt");

            if (! myFile.exists()) myFile.createNewFile();

            FileWriter fWriter = new FileWriter(myFile,true);
            fWriter.append(results + "\n");
            fWriter.close();
            Toast.makeText(ArrowErrors.this, "Result successfully stored!", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
            Log.e("asd", "Could not write file " + e.getMessage());
        }

        Intent intent = new Intent(ArrowErrors.this,MainActivity.class );
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        taskHandler.removeCallbacks(myTask);
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

            arrowErrorsView.moveBall(flag);
            arrowErrorsView.setTargetPosition();
            arrowErrorsView.invalidate();

            if (!taskCompleted) {
                taskHandler.postDelayed(myTask, FRAME_RATE);
            }else{
                taskHandler.removeCallbacks(myTask);
            }
        }
    };
}