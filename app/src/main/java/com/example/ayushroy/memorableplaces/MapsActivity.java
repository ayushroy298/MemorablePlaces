package com.example.ayushroy.memorableplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    Location last;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                if (location.getLatitude() != last.getLatitude() || location.getLongitude() != last.getLongitude() || location.getAltitude() != last.getAltitude())
                {
                    mMap.clear();
                    LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(curr).title("Your Current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            last=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        LatLng curr = new LatLng(last.getLatitude(), last.getLongitude());
        mMap.addMarker(new MarkerOptions().position(curr).title("Your Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));


    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        String address = "BLANK";

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try
        {
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
            if (listAddress != null && listAddress.size() > 0)
            {
                address="";
                if (listAddress.get(0).getFeatureName() != null)
                    address += listAddress.get(0).getFeatureName() + " ";
                if (listAddress.get(0).getLocality() != null)
                    address += listAddress.get(0).getLocality() + " ";
                if (listAddress.get(0).getAdminArea() != null)
                    address += listAddress.get(0).getAdminArea() + " ";

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        setResult(Activity.RESULT_OK, intent);
        intent.putExtra("Name", address);
        intent.putExtra("LatLng", latLng);
        //Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show();
        finish();

    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
