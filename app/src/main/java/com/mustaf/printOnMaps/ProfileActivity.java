package com.mustaf.printOnMaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Profile Settings");

        setContentView(R.layout.activity_profile);

        //findViewById(R.id.buttonPrintProjectCreation).setOnClickListener(
              //  v -> startActivity(new Intent(MainMenuActivity.this, PrintProjectCreationActivity.class)));

    }
}