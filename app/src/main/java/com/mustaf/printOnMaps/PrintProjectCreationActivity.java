package com.mustaf.printOnMaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mustaf.printOnMaps.model.Address;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mustaf.printOnMaps.MainMenuActivity.retrofit;

public class PrintProjectCreationActivity extends AppCompatActivity {

    private static final String TAG = PrintProjectCreationActivity.class.getSimpleName();
    static final String BASE_URL = "http://localhost:8080";
    private String address;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Create a new 3D project");
        setContentView(R.layout.activity_create_print_project);
        findViewById(R.id.buttonCreateProject).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postAddress();
                    }
                }

        );
        initializePlaces();
        setupAutocompleteFragment();
    }

    private void postAddress() {
        if (address == null) {
            return;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        // below line is to create an instance for our retrofit api class.
        PrintProjectApiService printProjectApiService = retrofit.create(PrintProjectApiService.class);

        // passing data from our text fields to our modal class
        Address address = new Address(this.address, this.lat, this.lng);

        // calling a method to create a post and passing our modal class.
        Call<Address> call = printProjectApiService.createAddress(address);

        // on below line we are executing our method.
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                // this method is called when we get response from our api.
                Toast.makeText(PrintProjectCreationActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Address> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    private void initializePlaces() {
        final String placesApiKey = BuildConfig.PLACES_API_KEY;
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), placesApiKey);
        }
    }

    private void setupAutocompleteFragment() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                address = place.getAddress();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                Log.i(TAG, "Place: " + place.getAddress() + ", lat: " + lat + ", " + lng);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


}