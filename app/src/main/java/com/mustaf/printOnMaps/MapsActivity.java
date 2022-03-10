package com.mustaf.printOnMaps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mustaf.printOnMaps.databinding.ActivityMapsBinding;
import com.mustaf.printOnMaps.model.Event;
import com.mustaf.printOnMaps.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.var;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Maps SDK for Android Quickstart
 * <p>url: https://developers.google.com/maps/documentation/android-sdk/start#look_at_the_code
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final String TAG = MapsActivity.class.getSimpleName();
    static Retrofit retrofit;
    static final String BASE_URL = "http://localhost:8080";
    ApiService apiService;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Map<String, List<Marker>> markersByUserId;

    void addUserDataToMarker(String userId, Marker marker) {
        apiService = retrofit.create(ApiService.class);
        Call<User> call = apiService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                var user = response.body();
                marker.setSnippet(user.getEmail());
                Log.d(TAG, "GET user request successful");
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        apiService = retrofit.create(ApiService.class);

        markersByUserId = new HashMap<>();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        Log.d(TAG, "onmapready");
        mMap = googleMap;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        centerMapToUserPosition();
        requestData();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull @NotNull Marker marker) {
                String userIdOfMarker = "";
                for (var entry : markersByUserId.entrySet()) {
                    if (entry.getValue().contains(marker)) {
                        userIdOfMarker = entry.getKey();
                    }
                }
                if (userIdOfMarker.isEmpty()) {
                    Log.e(TAG, "unknown user id");
                }
                //addUserDataToMarker(userIdOfMarker, marker);
                Intent startUserProfile = new Intent(MapsActivity.this, UserProfileActivity.class);
                startUserProfile.putExtra("userId", userIdOfMarker);
                startActivity(startUserProfile);
            }
        });
    }

    private void requestData() {
        Call<List<Event>> call = apiService.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                for (var event : response.body()) {
                    var latLng = new LatLng(event.getLat(), event.getLng());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(event.getName()));
                    var markers = markersByUserId.get(event.getPersonId());
                    if (markers == null) {
                        markersByUserId.put(event.getPersonId(), new ArrayList<>());
                    }
                    markersByUserId.get(event.getPersonId()).add(marker);
                }
                for (var entry: markersByUserId.entrySet()) {
                    Log.d(TAG, entry.getKey() + " : " + entry.getValue());
                }
                Log.d(TAG, "GET request successful");
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    /**
     * Access position of the current user and and use this data to center the map.
     */
    private void centerMapToUserPosition() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 15));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(-33.8523341, 151.2106085), 15));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}