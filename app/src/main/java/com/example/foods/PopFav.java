package com.example.foods;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PopFav extends Activity {
    private TextView nameTV;
    private DatabaseReference dbRef;
    private FirebaseDatabase database;
    private String cuid, name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popfav);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.6),(int)(height *.2));

      //

        name = getIntent().getStringExtra("name");

        nameTV = findViewById(R.id.labelTV);

        nameTV.setText(name);
        nameTV.setTypeface(null, Typeface.BOLD);

        Button deleteButton = (Button) findViewById(R.id.delBtn);
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cuid = user.getUid();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbRef = database.getReference("data");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            String uid =  snapshot.child("uid").getValue().toString();
                            if(uid.equals(cuid))
                            {
                                if(name.equals(snapshot.child("name").getValue().toString()))
                                {
                                snapshot.getRef().removeValue();
                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
//                String name = getIntent().getStringExtra("name");
//
//
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String uid = user.getUid();
//                dbRef.child(String.valueOf((maxid+1))).child("uid").setValue(uid);
            }
        });
    }
}
