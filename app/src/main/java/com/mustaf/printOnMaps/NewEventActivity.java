package com.mustaf.printOnMaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.mustaf.printOnMaps.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import lombok.var;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mustaf.printOnMaps.MainMenuActivity.retrofit;

public class NewEventActivity extends AppCompatActivity {

    private static final String TAG = NewEventActivity.class.getSimpleName();
    static final String BASE_URL = "http://localhost:8080";
    private String address;
    private double lat;
    private double lng;
    private Event event;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Create a new 3D project");
        setContentView(R.layout.activity_event_creation);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select a start date");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        findViewById(R.id.pick_start_date_button).setOnClickListener(
                v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER")
        );
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> ((TextView) findViewById(R.id.show_selected_start_date)).setText(materialDatePicker.getHeaderText()));

        findViewById(R.id.buttonCreateProject).setOnClickListener(
                view -> postAddress()
        );


        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);

        findViewById(R.id.address_button).setOnClickListener(
                v -> startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        );

        initializePlaces();
        setupAutocompleteFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                address = place.getAddress();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                ((TextView) findViewById(R.id.show_selected_address)).setText(address);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postAddress() {
        if (!isValidUserInput()) {
            notifyUserErrors();
            return;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        Event event = createEventFromForm();

        // below line is to create an instance for our retrofit api class.
        ApiService apiService = retrofit.create(ApiService.class);

        Log.e(TAG, "date: " + event.getEndDate().toString() + " , " + event.getStartDate().toString());

        // calling a method to create a post and passing our modal class.
        Call<Event> call = apiService.createEvent(event);

        // on below line we are executing our method.
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // this method is called when we get response from our api.
                Toast.makeText(NewEventActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable throwable) {
                Log.e(TAG, "test");
                Log.e(TAG, throwable.toString());
            }
        });
    }

    private void notifyUserErrors() {
        if (address == null || address.isEmpty()) {
            //todo
        }
        var eventName = ((EditText) findViewById(R.id.inputEventName)).getText().toString();
        if (eventName.isEmpty()) {
            //todo
        }
        try {
            LocalDate startDate = LocalDate.parse(((TextView) findViewById(R.id.show_selected_start_date)).getText().toString());
        } catch (DateTimeParseException e) {
            //todo
        }
        try {
            LocalDate endDate = LocalDate.parse(((TextView) findViewById(R.id.show_selected_end_date)).getText().toString());
        } catch (DateTimeParseException e) {
            //todo
        }
    }

    private boolean isValidUserInput() {
        try {
            var eventName = ((EditText) findViewById(R.id.inputEventName)).getText().toString();
            LocalDate startDate = LocalDate.parse(((TextView) findViewById(R.id.show_selected_start_date)).getText().toString());
            LocalDate endDate = LocalDate.parse(((TextView) findViewById(R.id.show_selected_end_date)).getText().toString());
            return address != null && !eventName.isEmpty();
        } catch (DateTimeParseException e) {
            Log.e(TAG, "parse exception thrown: " + ((TextView) findViewById(R.id.show_selected_start_date)).getText().toString()
                    + ", " + ((TextView) findViewById(R.id.show_selected_end_date)).getText().toString());
            return false;
        }
    }

    private Event createEventFromForm() {
        var personId = getSharedPreferences("userData", Context.MODE_PRIVATE).getString("personId", "");
        var name = ((EditText) findViewById(R.id.inputEventName)).getText().toString();
        var startDateStr = ((TextView) findViewById(R.id.show_selected_start_date)).getText().toString();
        var endDateStr = ((TextView) findViewById(R.id.show_selected_end_date)).getText().toString();

        return new Event(name,
                address,
                lat,
                lng,
                startDateStr,
                endDateStr,
                personId);
    }

    private void initializePlaces() {
        final String placesApiKey = BuildConfig.PLACES_API_KEY;
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), placesApiKey);
        }
    }

    private void setupAutocompleteFragment() {/*
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
        });*/
    }


}