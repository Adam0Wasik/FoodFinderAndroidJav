package com.example.foods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView distValue, priceValue, tagBox;
    private SeekBar priceBar, distBar;
    private Toolbar toolbar;
    private SupportMapFragment supporMapFragment;
    private GoogleMap map;
    private float radius;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat;
    private double currentLong;
    private DatabaseReference dbRef;
    private FirebaseDatabase database;
    private String cuid;
    private double latt, lngg;
    private Location location2;
    private double cLat, cLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cuid = user.getUid();

        toolbar = findViewById(R.id.mToolBar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.searchlabel);
        mTitle.setTypeface(null, Typeface.BOLD);

        toolbar.setNavigationIcon(R.drawable.ic_star_white_24dp);
        toolbar.setTitle("");// override appname
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
                startActivity(intent);
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        tagBox = (TextView) findViewById(R.id.tagBox);

        distBar = (SeekBar) findViewById(R.id.barDistance);
        distValue = (TextView) findViewById(R.id.distValue);

        distBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue/4.0;
                distValue.setText( progress + "km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

///
    priceBar = (SeekBar) findViewById(R.id.barPrice);
    priceValue = (TextView) findViewById(R.id.priceValue2);

        priceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        int progress = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progress = progresValue*2;
            priceValue.setText( progress + "PLN");
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    });
///
        Button clearButton = (Button) findViewById(R.id.clrButton);
        clearButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              priceBar.setProgress(0);
              distBar.setProgress(0);
              tagBox.setText("");
              map.clear();
              currentLat = location2.getLatitude();
              currentLong = location2.getLongitude();
              locateAndZoom("You are here", currentLat, currentLong, 15);

            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Button searchBtn = (Button) findViewById(R.id.srchButton);
        searchBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radius = distBar.getProgress();
                String keyword = String.valueOf(tagBox.getText());
                radius = radius * 250;
                int price=priceBar.getProgress();
                price *= 2;
                String currLat = Double.toString(currentLat);
                String currLng = Double.toString(currentLong);

                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=" + currLat + "," + currLng +
                        "&radius="  + radius +
                        "&type=restaurant"+
                        "&keyword=" + keyword +
                        "&maxprice=" + price +
                        "&sensor=true" +
                        "&key=" + getResources().getString(R.string.google_map_api_key);
                new PlaceTask().execute(url);


            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        supporMapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //When Granted
            getCurrentLocation();
        }else{
            //Request Permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        database = FirebaseDatabase.getInstance();
}

    private void getCurrentLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
               //when succes
                 location2 = location;
                if (location2 != null){
                    supporMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {

                            if(getIntent().hasExtra("name")) {
                                dbRef = database.getReference("data");
                                dbRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                        {
                                            String uid =  snapshot.child("uid").getValue().toString();
                                            if(uid.equals(cuid))
                                            {
                                                if(getIntent().getStringExtra("name").equals(snapshot.child("name").getValue().toString()))
                                                {
                                                    cLat = Double.parseDouble(snapshot.child("lat").getValue().toString());
                                                    cLng = Double.parseDouble(snapshot.child("lng").getValue().toString());
                                                    map = googleMap;
                                                    locateAndZoom(getIntent().getStringExtra("name"),cLat,cLng,15);
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                    }
                                });
                            }
                            else
                            {
                                currentLat = location2.getLatitude();
                                currentLong = location2.getLongitude();
                                map = googleMap;
                                locateAndZoom("You are here", currentLat,currentLong,15);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                this.startActivity(intent2);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }
    public void locateAndZoom(String name, double lat, double lng, int zoomLvl){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat,lng),zoomLvl
        ));
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(lat,lng));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        options.title(name);
        map.addMarker(options);
    }
    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                 data = downloadUrl(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new PareserTask().execute(s);
        }
    }

    private String downloadUrl(String string)throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line  = "";
        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }

    private class PareserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>>{
        @Override
        protected  List<HashMap<String,String>> doInBackground(String... strings) {
            JsonPareser jsonPareser = new JsonPareser();
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonPareser.parseResult(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
             map.clear();
             for(int i = 0; i<hashMaps.size(); i++){
                 HashMap<String,String> hashMapList = hashMaps.get(i);
                 double lat = Double.parseDouble(hashMapList.get("lat"));
                 double lng = Double.parseDouble(hashMapList.get("lng"));
                 String name = hashMapList.get("name");
                 LatLng latLng = new LatLng(lat,lng);
                 MarkerOptions options = new MarkerOptions();
                 options.position(latLng);
                 options.title(name);

                 map.addMarker(options);
                 MarkerOptions me = new MarkerOptions();
                 me.position(new LatLng(currentLat, currentLong));
                 me.title("You are here");
                 me.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                 map.addMarker(me);
                 CircleOptions circleOptions = new CircleOptions()
                         .center(new LatLng(currentLat, currentLong))
                         .radius(radius);
                 Circle circle = map.addCircle(circleOptions);
                 map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                         circleOptions.getCenter(),getZoomLevel(circle)
                 ));
                 map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                     @Override
                     public boolean onMarkerClick(Marker marker) {
                         Intent pop = new Intent(MainActivity.this, PopAdd.class);
                         LatLng ltng = marker.getPosition();
                         String temp = ltng.toString();
                         temp =  temp.replace("lat/lng: (","");
                         temp = temp.replace(")","");
                         String[] T = temp.split(",");


                         pop.putExtra("lat", T[0]);
                         pop.putExtra("lng", T[1]);
                         pop.putExtra("name", marker.getTitle());

                         startActivity(pop);
                         return false;
                     }
                 });
             }

        }
    }
    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
}
