package hr.riteh.nksproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class ArrowTime extends AppCompatActivity {

    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_time);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        leftButton = findViewById(R.id.leftButtonAT);
        rightButton = findViewById(R.id.rightButtonAT);
        time = findViewById(R.id.timeAT);


        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //increaseSize();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //resetSize();
                }
                return true;
            }
        });

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //increaseSize();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //resetSize();
                }
                return true;
            }
        });

    }
}
