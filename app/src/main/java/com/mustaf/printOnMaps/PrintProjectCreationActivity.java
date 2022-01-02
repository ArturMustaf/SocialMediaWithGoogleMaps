package com.mustaf.printOnMaps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PrintProjectCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Create a new 3D project");

        setContentView(R.layout.activity_create_print_project);
    }
}