package com.mustaf.printOnMaps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mustaf.printOnMaps.model.PrintProject;

import java.util.List;

import lombok.var;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainMenuActivity extends AppCompatActivity {

    static final String TAG = MainMenuActivity.class.getSimpleName();
    static Retrofit retrofit = null;
    static final String BASE_URL = "http://localhost:8080";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Main menu");

        setContentView(R.layout.activity_main_menu);

        setButtonOnClickListener();
    }

    private void setButtonOnClickListener() {
        findViewById(R.id.buttonPrintProjectCreation).setOnClickListener(
                v -> startActivity(new Intent(MainMenuActivity.this, PrintProjectCreationActivity.class)));

        findViewById(R.id.buttonMaps).setOnClickListener(
                v -> startActivity(new Intent(MainMenuActivity.this, MapsActivity.class)));

        findViewById(R.id.buttonProfile).setOnClickListener(
                v -> startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class)));

        findViewById(R.id.buttonGET).setOnClickListener(
                v -> connect());
    }

    private void connect() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PrintProjectApiService printProjectApiService = retrofit.create(PrintProjectApiService.class);
        Call<List<PrintProject>> call = printProjectApiService.getAllPrintProjects();
        call.enqueue(new Callback<List<PrintProject>>() {
            @Override
            public void onResponse(Call<List<PrintProject>> call, Response<List<PrintProject>> response) {
                StringBuilder sb = new StringBuilder();
                sb.append("GET print projects from server:\n");
                for (var printProject: response.body()) {
                    sb.append("Project: " + printProject.name +
                            "\nDescription: " + printProject.description +
                            "\nURL: " + printProject.sourceUrl);
                    sb.append("\n---------------\n");
                }
                TextView tv = findViewById(R.id.textView);
                tv.setText(sb.toString());
            }
            @Override
            public void onFailure(Call<List<PrintProject>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }
}
