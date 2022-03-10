package com.mustaf.printOnMaps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mustaf.printOnMaps.model.User;

import lombok.var;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();
    static Retrofit retrofit;
    static final String BASE_URL = "http://localhost:8080";
    ApiService apiService;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        requestUser();
    }

    private void setText(int rId, String text) {
        ((TextView) findViewById(rId)).setText(text);
    }

    private void requestUser() {
        apiService = retrofit.create(ApiService.class);
        Call<User> call = apiService.getUserById(getIntent().getStringExtra("userId"));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                fillUserDetails();
                Log.d(TAG, "GET user request successful");
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });

    }

    private void fillUserDetails() {
        setTitle(user.getDisplayName());
        setText(R.id.userName, user.getName());
        setText(R.id.userSurname, user.getSurname());
        setText(R.id.userEmail, user.getEmail());
    }
}