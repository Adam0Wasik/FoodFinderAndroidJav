package com.example.foods;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopAdd extends Activity {
    private TextView nameTV;
    private DatabaseReference dbRef;
    private FirebaseDatabase database;
    private  int maxid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popadd);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.6),(int)(height *.2));
        String lat = getIntent().getStringExtra("lat");
        String lng = getIntent().getStringExtra("lng");
        String name = getIntent().getStringExtra("name");

        nameTV = findViewById(R.id.labelTV);

        nameTV.setText(name);
        nameTV.setTypeface(null, Typeface.BOLD);

        Button addButton = (Button) findViewById(R.id.addBtn);
        database = FirebaseDatabase.getInstance();
        maxid = 0;
        dbRef = database.getReference("data");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid= (int) snapshot.getChildrenCount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getIntent().getStringExtra("name");
                dbRef.child(String.valueOf((maxid+1))).child("name").setValue(name);

                String lat = getIntent().getStringExtra("lat");
                dbRef.child(String.valueOf((maxid+1))).child("lat").setValue(lat);

                String lng = getIntent().getStringExtra("lng");
                dbRef.child(String.valueOf((maxid+1))).child("lng").setValue(lng);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                dbRef.child(String.valueOf((maxid+1))).child("uid").setValue(uid);

            }
        });
    }
}
