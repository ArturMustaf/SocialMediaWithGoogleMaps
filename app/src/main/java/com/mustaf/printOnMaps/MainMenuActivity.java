package com.mustaf.printOnMaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        setButtonOnClickListener();
    }

    private void setButtonOnClickListener() {
        findViewById(R.id.buttonPrintProjectCreation).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainMenuActivity.this, PrintProjectCreationActivity.class));
                    }
                });

        findViewById(R.id.buttonMaps).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainMenuActivity.this, MapsActivity.class));
                    }
                });

        findViewById(R.id.buttonProfile).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class));
                    }
                });
    }
}
