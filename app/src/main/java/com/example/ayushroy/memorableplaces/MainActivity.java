package com.example.ayushroy.memorableplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public void MapSwitch(View View)
    {
        Intent intent =new Intent(getApplicationContext(),MapsActivity.class);
        startActivityForResult(intent,1);
    }

    ArrayList<String> places;
    ArrayList<LatLng> latLngArray;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);
        places= new ArrayList<>();
        latLngArray=new ArrayList<>();

        Button button = findViewById(R.id.button);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(getApplicationContext(),PlaceMapsActivity.class);
                intent.putExtra("Name", places.get(position));
                intent.putExtra("LatLng", latLngArray.get(position));
                startActivity(intent);
            }
        });

    }

    SharedPreferences sharedPreferences;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        sharedPreferences=this.getSharedPreferences("com.example.ayushroy.memorableplaces",Context.MODE_PRIVATE);

        if(resultCode == Activity.RESULT_OK)
        {
            String place = data.getStringExtra("Name");
            LatLng latLng=data.getExtras().getParcelable("LatLng");
            places.add(place);
            latLngArray.add(latLng);

            try
            {
                sharedPreferences.edit().putString("places",ObjectSerializer.serialize(places)).apply();
                //sharedPreferences.edit().putString("latLng",ObjectSerializer.serialize(latLngArray.toString())).apply();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //Toast.makeText(this, place +" "+ latLng.toString(), Toast.LENGTH_SHORT).show();

            ArrayList<String> newPlaces=new ArrayList<>();
            //ArrayList<String> newlatLngArray=new ArrayList<>();

            try
            {
                newPlaces =(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
                //newlatLngArray =(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latLng",ObjectSerializer.serialize(new ArrayList<String>())));
                Log.i("Chacha",newPlaces.toString());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            Toast.makeText(this, newPlaces.toString(), Toast.LENGTH_SHORT).show();

            arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,places);
            listView.setAdapter(arrayAdapter);

        }

    }
}
