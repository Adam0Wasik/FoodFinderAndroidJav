package com.example.foods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView list ;
    private ArrayAdapter<String> adapter ;
    private DatabaseReference dbRef;
    private FirebaseDatabase database;
    private  String cuid;
    private List<LocationFav> locations;
    private ArrayList<String> names ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cuid = user.getUid();
        names = new ArrayList<String>();

        toolbar=findViewById(R.id.mToolBar);
        setSupportActionBar(toolbar);

        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Favourites");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        list = (ListView) findViewById(R.id.listView1);
        locations = new ArrayList<LocationFav>();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("data");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                names.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    String uid =  snapshot.child("uid").getValue().toString();
                    if(uid.equals(cuid))
                    {
                       String name = snapshot.child("name").getValue().toString();
                       double lat = Double.parseDouble(snapshot.child("lat").getValue().toString());
                       double lng = Double.parseDouble(snapshot.child("lng").getValue().toString());
                       LocationFav obj = new LocationFav(name,lat,lng);
                       locations.add(obj);
                       names.add(name);
                    }
                }
                List<String> listWithoutDuplicates = new ArrayList<>(
                        new HashSet<>(names));
                adapter = new ArrayAdapter<String>(FavouritesActivity.this, R.layout.single_row, listWithoutDuplicates);
                list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


    }
}
