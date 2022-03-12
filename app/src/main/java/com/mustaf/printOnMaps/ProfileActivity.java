package com.mustaf.printOnMaps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import lombok.var;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Profile Settings");

        setContentView(R.layout.activity_profile);

        setupTabLayout();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void setupTabLayout() {
        var tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent;
                switch (tab.getPosition()) {
                    case 0:
                        intent = new Intent(ProfileActivity.this, MainMenuActivity.class);
                        break;
                    case 1:
                        intent = new Intent(ProfileActivity.this, NewEventActivity.class);
                        break;
                    case 2:
                        intent = new Intent(ProfileActivity.this, MapsActivity.class);
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
        TabLayout.Tab activeTab = tabLayout.getTabAt(3);
        tabLayout.selectTab(activeTab);
    }
}