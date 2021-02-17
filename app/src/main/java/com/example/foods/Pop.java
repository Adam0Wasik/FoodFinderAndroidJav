package com.example.foods;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Pop extends Activity {
    private TextView nameTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.pop);
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);

    int width = dm.widthPixels;
    int height = dm.heightPixels;

    getWindow().setLayout((int)(width*.6),(int)(height *.1));
    String lat = getIntent().getStringExtra("lat");
    String lng = getIntent().getStringExtra("lng");
    String name = getIntent().getStringExtra("name");


    nameTV = findViewById(R.id.nameid);

    nameTV.setText(name);
    nameTV.setTypeface(null, Typeface.BOLD);
    }
}
