package com.example.foods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class FavouritesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView list ;
    private ArrayAdapter<String> adapter ;
    private DatabaseReference dbRef;
    private FirebaseDatabase database;
    private String cuid;
    private List<LocationFav> locations;
    private ArrayList<String> names;
    private String name;
    private double lat;
    private double lng;
    private int position2;
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
        mTitle.setText(R.string.favourites);
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

        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(FavouritesActivity.this, PopFav.class);
                String name = names.get(position);
                intent.putExtra("name", name);
                position2 = position;
                dbRef = database.getReference("data");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            String uid =  snapshot.child("uid").getValue().toString();
                            if(uid.equals(cuid))
                            {
                                if(names.get(position2).equals(snapshot.child("name").getValue().toString()))
                                {
                                    lat = Double.parseDouble(snapshot.child("lat").getValue().toString());
                                    lng = Double.parseDouble(snapshot.child("lng").getValue().toString());

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                startActivity(intent);
            }
        });
    }

}
