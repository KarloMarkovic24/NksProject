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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class TiltTime extends AppCompatActivity {

    ImageButton timeB;
    TiltTimeView tiltTimeView;
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
    private static final int NUMBER_OF_TASKS = 3;
    boolean taskCompleted=true;
    ArrayList<Long> times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_time);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics myDisplaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDisplaymetrics);
        final int screenHeight = myDisplaymetrics.heightPixels;
        final int screenWidth = myDisplaymetrics.widthPixels;
        LinearLayout mainView = findViewById(R.id.viewTT);
        timeB = findViewById(R.id.timeTT);


        radius = screenHeight / 36;

        tiltTimeView = new TiltTimeView(this);
        tiltTimeView.setBallRadius((float) radius);
        tiltTimeView.setCenterPosition(screenWidth/2,screenHeight/2);
        tiltTimeView.setBallPosition(screenWidth/2-radius,screenHeight/2-radius);
        tiltTimeView.setTargets();
        tiltTimeView.setTargetPosition();
        mainView.addView(tiltTimeView);
        times = new ArrayList<>();



        timeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskCompleted=false;
                taskHandler.postDelayed(myTask, FRAME_RATE);
                timeB.setVisibility(View.INVISIBLE);
                startTask = System.currentTimeMillis();
            }
        });

        tiltTimeView.setBallInWallListener(new TiltTimeView.ballInWallListener() {
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
                        tiltTimeView.setBallPosition(screenWidth/2-radius,screenHeight/2-radius);
                        tiltTimeView.nextTarget();
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TiltTime.this);
        String userName = preferences.getString("username","");
        String results = (userName + " " + "TiltTime \n");

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
            Toast.makeText(TiltTime.this, "Result successfully stored!", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
            Log.e("asd", "Could not write file " + e.getMessage());
        }

        Intent intent = new Intent(TiltTime.this,MainActivity.class );
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

            tiltTimeView.moveBall(flag);
            tiltTimeView.setTargetPosition();
            tiltTimeView.invalidate();

            if (!taskCompleted) {
                taskHandler.postDelayed(myTask, FRAME_RATE);
            }else{
                taskHandler.removeCallbacks(myTask);
            }
        }
    };
}
