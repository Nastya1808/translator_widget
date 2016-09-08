package com.example.nastya.translator_widget;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Switch turnOnSwitch;
    private String currentLanguageFrom;
    private String currentLanguageTo;
    private SharedPreferences preferences;
    public static final String PREFERENCES = "translator_settings";
    public static final String PREFERENCES_SPINNER_FROM = "spinner_from";
    public static final String PREFERENCES_SPINNER_TO = "spinner_to";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(PREFERENCES_SPINNER_FROM) && preferences.contains(PREFERENCES_SPINNER_TO)){
            currentLanguageFrom = preferences.getString(PREFERENCES_SPINNER_FROM, "");
            currentLanguageTo = preferences.getString(PREFERENCES_SPINNER_TO, "");
        }
        else {
            currentLanguageFrom = "English";
            currentLanguageTo = "Russian";
        }


        turnOnSwitch = (Switch)findViewById(R.id.turnOn);

            turnOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED) {
                            onPermissionsGranted();
                        } else {
                            requestPermission();
                        }
                    } else {
                        stopService(new Intent(MainActivity.this, OverlayService.class));
                    }
                }
            });

        spinnerFrom = (Spinner)findViewById(R.id.spinnerFrom);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(currentLanguageFrom);
        spinnerFrom.setSelection(spinnerPosition);

        spinnerTo = (Spinner)findViewById(R.id.spinnerTo);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.languages_list, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapter2);
        spinnerPosition = adapter.getPosition(currentLanguageTo);
        spinnerTo.setSelection(spinnerPosition);

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), spinnerFrom.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                currentLanguageFrom = spinnerFrom.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), spinnerTo.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                currentLanguageTo = spinnerTo.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_SPINNER_FROM, currentLanguageFrom);
        editor.putString(PREFERENCES_SPINNER_TO, currentLanguageTo);
        editor.apply();
    }

    private void onPermissionsGranted() {
        Intent intent = new Intent(this, OverlayService.class);
        intent.putExtra("from", Langueges.convert(currentLanguageFrom));
        intent.putExtra("to", Langueges.convert(currentLanguageTo));

        startService(intent);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                PERMISSION_REQUEST_CODE);
        findViewById(R.id.frameLayout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            } else {
                onPermissionsGranted();
            }
        }
    }

}
