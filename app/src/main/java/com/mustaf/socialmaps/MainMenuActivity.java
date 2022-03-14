package com.mustaf.socialmaps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.mustaf.socialmaps.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
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
    List<Event> eventsList;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Main menu");

        setContentView(R.layout.activity_main_menu);

        setupTabLayout();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getAllEvents();
    }

    private void setupTabLayout() {
        var tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent;
                switch (tab.getPosition()) {
                    case 1:
                        intent = new Intent(MainMenuActivity.this, NewEventActivity.class);
                        break;
                    case 2:
                        intent = new Intent(MainMenuActivity.this, MapsActivity.class);
                        break;
                    case 3:
                        intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
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
        TabLayout.Tab activeTab = tabLayout.getTabAt(0);
        tabLayout.selectTab(activeTab);
    }


    private void getAllEvents() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Event>> call = apiService.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                Log.d(TAG, "onResponse");
                eventsList = response.body();
                showEvents();
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable throwable) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, throwable.toString());
            }
        });
    }


    private void showEvents() {
        List<String> list = new ArrayList<>();

        for (var event : eventsList) {
            LocalDateTime start = LocalDateTime.parse(event.getStartDate());
            String startDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(start);
            String startTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(start);

            LocalDateTime end = LocalDateTime.parse(event.getEndDate());
            String endDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(end);
            String endTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(end);

            String listItem = "\n" + event.getName() + "\n\n"
                    + "from: " + startDate + "   "
                    + startTime + "\n"
                    + "until: " + endDate + "   "
                    + endTime + "\n\n"
                    + event.getAddress() + "\n";
            list.add(listItem);
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                list);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            var event = eventsList.get(position);
            Intent intent = new Intent(MainMenuActivity.this, MapsActivity.class);
            intent.putExtra("lat", event.getLat());
            intent.putExtra("lng", event.getLng());
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }
}
