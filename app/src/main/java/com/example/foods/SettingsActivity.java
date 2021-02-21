package com.example.foods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Toolbar toolbar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        toolbar = findViewById(R.id.mToolBar);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);
        toolbar.setTitle("");// override appname
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.settings);
        mTitle.setTypeface(null, Typeface.BOLD);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               recreate();
            }
        });
        Spinner dropdownLng = findViewById(R.id.langSpinner);
        String[] langs = new String[]{"Polski", "English", };
        ArrayAdapter<String> langsadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, langs);
        dropdownLng.setAdapter(langsadapter);

        dropdownLng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        LocaleHelper.setLocale(SettingsActivity.this, "pl");
                        break;
                    case 1:
                        LocaleHelper.setLocale(SettingsActivity.this, "en");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });


        Spinner dropdownUnit = findViewById(R.id.unitSpinner);
        String[] units = new String[]{"imperial", "metric" };
        ArrayAdapter<String> unitsadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        dropdownUnit.setAdapter(unitsadapter);
        dropdownUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });

        Utils.onActivityCreateSetTheme(this);
        findViewById(R.id.themeBtn1).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.themeBtn2).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.themeBtn3).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.themeBtn4).setOnClickListener((View.OnClickListener) this);
    }
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.themeBtn1:
                Utils.changeToTheme(this, Utils.THEME_DEFAULT);

                break;
            case R.id.themeBtn2:
                Utils.changeToTheme(this, Utils.THEME_YELLOW);
                break;
            case R.id.themeBtn3:
                Utils.changeToTheme(this, Utils.THEME_GREEN);
            case R.id.themeBtn4:
                Utils.changeToTheme(this, Utils.THEME_VIOLET);
                break;
        }
    }
}
