package com.example.himanshusaluja.googlemap;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_ll);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        createLocationRequest();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            LatLng location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(location).title("Marker "+countryName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(location);
            circleOptions.radius(200);
            circleOptions.fillColor(getResources().getColor(R.color.colorAccent));
            mMap.addCircle(circleOptions);

            Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude()+" "+countryName,Toast.LENGTH_LONG).show();
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng location1 = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location1));
        mMap.addMarker(new MarkerOptions().position(location1).title("Marker "));
        CircleOptions circleOptions = new CircleOptions();
        LatLng location2 = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        circleOptions.center(location2);
        circleOptions.radius(200);
        circleOptions.fillColor(getResources().getColor(R.color.colorAccent));
        mMap.addCircle(circleOptions);
        Toast.makeText(this, "Update -> Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }


    }
}
