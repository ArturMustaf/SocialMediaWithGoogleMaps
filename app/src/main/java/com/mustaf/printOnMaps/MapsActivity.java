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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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

    private void setupTabLayout() {
        var tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent;
                switch (tab.getPosition()) {
                    case 0:
                        intent = new Intent(MapsActivity.this, MainMenuActivity.class);
                        break;
                    case 1:
                        intent = new Intent(MapsActivity.this, NewEventActivity.class);
                        break;
                    case 3:
                        intent = new Intent(MapsActivity.this, ProfileActivity.class);
                        break;
                    default:
                        return;
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        TabLayout.Tab activeTab = tabLayout.getTabAt(2);
        tabLayout.selectTab(activeTab);
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

        setupTabLayout();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onmapready");
        mMap = googleMap;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        centerMap();
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
                Intent startUserProfile = new Intent(MapsActivity.this, ProfileActivity.class);
                startUserProfile.putExtra("userSelected", userIdOfMarker);
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
                for (var entry : markersByUserId.entrySet()) {
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

    private void centerMap() {
        try {
            @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    double lat, lng;
                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("lat") && getIntent().getExtras().containsKey("lng")) {
                        lat = getIntent().getDoubleExtra("lat", -1);
                        lng = getIntent().getDoubleExtra("lng", -1);
                        Log.e(TAG, "A");
                    } else {
                        Log.e(TAG, "B");
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            Log.e(TAG, "C");
                            lat = lastKnownLocation.getLatitude();
                            lng = lastKnownLocation.getLongitude();
                        } else {
                            Log.e(TAG, "E");
                            lat = -33.8523341;
                            lng = 151.2106085;
                        }
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            lat, lng), 15));
                }
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            });
        } catch (
                SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}