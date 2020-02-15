package hr.riteh.nksproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class ArrowTime extends AppCompatActivity {

    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton timeB;
    ArrowTimeView arrowTimeView;
    Handler taskHandler;
    long time1;
    long time2;
    long taskTime;
    long startTask;
    long endTask;
    int timeFlag=0;
    int flag=0;
    int FRAME_RATE = 20;
    int radius;
    private static final int NUMBER_OF_TASKS = 20;
    boolean taskCompleted=true;
    ArrayList<Long> times;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_time);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics myDisplaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDisplaymetrics);
        final int screenHeight = myDisplaymetrics.heightPixels;
        final int screenWidth = myDisplaymetrics.widthPixels;
        LinearLayout mainView = findViewById(R.id.viewAT);
        leftButton = findViewById(R.id.leftButtonAT);
        rightButton = findViewById(R.id.rightButtonAT);
        timeB = findViewById(R.id.timeAT);

        radius = screenHeight / 36;

        arrowTimeView = new ArrowTimeView(this);
        arrowTimeView.setBallRadius((float) radius);
        arrowTimeView.setCenterPosition(screenWidth/2,screenHeight/2);
        arrowTimeView.setBallPosition(screenWidth/2-radius,screenHeight/2-radius);
<<<<<<< HEAD
        arrowTimeView.setTargets(screenWidth);
=======
        arrowTimeView.setTargets(screenWidth, screenHeight);
>>>>>>> 2845ba9225c2b816473b75ee54f8ef43f72c2029
        arrowTimeView.setTargetPosition();
        mainView.addView(arrowTimeView);
        times = new ArrayList<>();

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
                arrowTimeView.resetSpeed();
                taskHandler.postDelayed(myTask, FRAME_RATE);
                timeB.setVisibility(View.INVISIBLE);
                startTask = System.currentTimeMillis();
            }
        });

        arrowTimeView.setBallInWallListener(new ArrowTimeView.ballInWallListener() {
            @Override
            public void onBallInWall(String input) {

                if(input.equals("outside")){
                    timeFlag=0;
                    time1=0;
                    time2=0;
                }

                if (input.equals("inside")) {
                    if(timeFlag==0){
                        timeFlag=1;
                        time1=System.currentTimeMillis();
                    }

                    time2=System.currentTimeMillis();

                    if(time2-time1>500) {
                        endTask = System.currentTimeMillis();
                        taskCompleted=true;
                        arrowTimeView.setBallPosition(screenWidth/2-radius,screenHeight/2-radius);
                        arrowTimeView.nextTarget();
                        timeB.setVisibility(View.VISIBLE);
                        taskDone();
                    }
                }
            }
        });
        taskHandler = new Handler();
    }

    public void taskDone(){

        taskTime=endTask-startTask;
        times.add(taskTime);

        if(times.size() == NUMBER_OF_TASKS){
            testingDone();
        }
    }

    private void testingDone() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ArrowTime.this);
        String userName = preferences.getString("username","");
        String results = (userName + " " + "ArrowTime \n");

        for(int i=0;i<times.size();i++){
            results+=("Task "+(i+1)+": "+times.get(i)+"\n");
        }

        try {
            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File myFile = new File(sdCard, "results.txt");

            if (! myFile.exists()) myFile.createNewFile();

            FileWriter fWriter = new FileWriter(myFile,true);
            fWriter.append(results + "\n");
            fWriter.close();
            Toast.makeText(ArrowTime.this, "Result successfully stored!", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
            Log.e("asd", "Could not write file " + e.getMessage());
        }

        Intent intent = new Intent(ArrowTime.this,MainActivity.class );
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

            arrowTimeView.moveBall(flag);
            arrowTimeView.setTargetPosition();
            arrowTimeView.invalidate();

            if (!taskCompleted) {
                taskHandler.postDelayed(myTask, FRAME_RATE);
            }else{
                taskHandler.removeCallbacks(myTask);
            }
        }
    };
}
