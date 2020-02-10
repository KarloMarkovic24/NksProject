package hr.riteh.nksproject;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MaterialSpinner spinner_mode;
    MaterialSpinner spinner_measure;
    Button start;
    EditText userName;
    Integer modeSelected;
    Integer measureSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        setContentView(R.layout.activity_main);
                        spinner_mode = (MaterialSpinner) findViewById(R.id.spinner_mode);
                        spinner_measure = (MaterialSpinner) findViewById(R.id.spinner_measure);
                        start = (Button) findViewById(R.id.btn_start);
                        userName = (EditText) findViewById(R.id.user_name);

                        List<String> list_mode = new ArrayList<>();
                        list_mode.add("Arrows");
                        list_mode.add("Tilt");
                        List<String> list_measure = new ArrayList<>();
                        list_measure.add("Time");
                        list_measure.add("Errors");
                        spinner_mode.setItems(list_mode);
                        spinner_measure.setItems(list_measure);
                        start.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String strUserName = userName.getText().toString();
                                if(TextUtils.isEmpty(strUserName)) {
                                    userName.setError("Enter your name!");
                                    return;
                                }

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", strUserName);
                                editor.apply();
                                modeSelected = spinner_mode.getSelectedIndex();
                                measureSelected = spinner_measure.getSelectedIndex();

                                if(modeSelected == 0){ // arrows
                                    if(measureSelected == 0){//time

                                        Intent intent = new Intent(MainActivity.this,ArrowTime.class );
                                        startActivity(intent);
                                        finish();

                                    }else if(measureSelected == 1){//Errors

                                        Intent intent = new Intent(MainActivity.this,ArrowErrors.class );
                                        startActivity(intent);
                                        finish();

                                    }
                                }else if (modeSelected == 1){//tilt
                                    if(measureSelected == 0){//time

                                        Intent intent = new Intent(MainActivity.this,TiltTime.class );
                                        startActivity(intent);
                                        finish();

                                    }else if(measureSelected == 1) {//Errors

                                        Intent intent = new Intent(MainActivity.this, TiltErrors.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }

                            }
                        });


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();


    }
}