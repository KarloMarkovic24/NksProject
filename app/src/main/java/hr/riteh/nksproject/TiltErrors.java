package hr.riteh.nksproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.ImageButton;

public class TiltErrors extends AppCompatActivity {

    ImageButton time;
    OrientationEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_errors);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        time = findViewById(R.id.timeTE);



        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                Log.v("asd", "Orientation changed to " + orientation);

            }
        };

        if (mOrientationListener.canDetectOrientation() == true) {
            Log.v("asd", "Can detect orientation");
            mOrientationListener.enable();
        } else {
            Log.v("asd", "Cannot detect orientation");
            mOrientationListener.disable();
        }

        //on destroy
        mOrientationListener.disable();
    }
}
