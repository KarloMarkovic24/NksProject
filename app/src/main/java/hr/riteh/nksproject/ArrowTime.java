package hr.riteh.nksproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrowTime extends AppCompatActivity {

    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton timeB;
    ArrowTimeView arrowTimeView;
    Handler taskHandler;
    long time;
    int flag=0;
    int FRAME_RATE = 20;
    int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_time);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics myDisplaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDisplaymetrics);
        int screenHeight = myDisplaymetrics.heightPixels;
        int screenWidth = myDisplaymetrics.widthPixels;
        LinearLayout mainView = findViewById(R.id.viewAT);

        leftButton = findViewById(R.id.leftButtonAT);
        rightButton = findViewById(R.id.rightButtonAT);
        timeB = findViewById(R.id.timeAT);

        this.radius = screenHeight / 36;

        arrowTimeView = new ArrowTimeView(this);
        arrowTimeView.setBallRadius((float) radius);
        arrowTimeView.setCenterPosition(screenWidth/2,screenHeight/2);
        arrowTimeView.setBallPosition(screenWidth/2-radius,screenHeight/2-radius);
        arrowTimeView.setTargetPosition(screenWidth/2-radius+300,screenHeight/2-radius);
        mainView.addView(arrowTimeView);

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
                taskHandler.postDelayed(myTask, FRAME_RATE);
                timeB.setVisibility(View.INVISIBLE);
            }
        });




         /***
        // Listener za koliziju:
        arrowTimeView.setBallInWallListener(new arrowTimeView.ballInWallListener() {
            @Override
            public void onBallInWall(String cause) {
                if (cause.equals("wall")) {

                }
                else if (cause.equals("obstacle")) {
                    // ovdje cemo detektirati neku drugu vrstu kolizije;
                    // npr. ako prodjemo "kroz" neki objekt koji nije zid, onda
                    // mozemo biti nagradjeni novim zivotom

                }
            }
        });  ***/



        // taskHandler je objekt koji ce nam upravljati periodičnim zadavanjem zadataka iscrtavanja
        // informacija na UI (i pripadnim redom poruka)
        // (each Handler instance is associated with a single thread and that thread's message queue):
        taskHandler = new Handler();

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
        // Runnable objekt mora imati definiranu metodu 'run':
        @Override
        public void run(){

            arrowTimeView.MoveBall(flag);
            arrowTimeView.invalidate();      // zakazi novo iscrtavanje custom view-a

            // Ako zivot nije izgubljen (nema kolizije sa zidom), igra "dobiva novi frame":
            taskHandler.postDelayed(myTask, FRAME_RATE);


        }
    };



}
