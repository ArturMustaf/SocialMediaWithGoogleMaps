package com.mustaf.socialmaps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mustaf.socialmaps.model.User;
import com.squareup.picasso.Picasso;

import lombok.var;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();
    static Retrofit retrofit;
    static final String BASE_URL = "http://localhost:8080";
    ApiService apiService;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Profile Settings");

        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setupTabLayout();

        setupRetrofit();

        requestUser();

    }

    private void setupMailButton() {
        if (findViewById(R.id.button_email).getVisibility() != View.GONE) {
            ((FloatingActionButton) findViewById(R.id.button_email)).setOnClickListener(
                    view -> {
                        Intent sendMail = new Intent(Intent.ACTION_SENDTO);
                        sendMail.setData(Uri.parse("mailto:")); // only email apps should handle this
                        sendMail.putExtra(Intent.EXTRA_EMAIL, new String[] { user.getEmail() });
                        startActivity(sendMail);
                    }
            );
        }
    }

    private void setupRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        apiService = retrofit.create(ApiService.class);
    }

    private void requestUser() {
        String userId;
        if (getIntent().getExtras() != null
                && getIntent().getExtras().containsKey("userSelected")
                && !getIntent().getStringExtra("userSelected").equals(getSharedPreferences("userData", Context.MODE_PRIVATE).getString("personId", ""))) {
            userId = getIntent().getStringExtra("userSelected");
        } else {
            userId = getSharedPreferences("userData", Context.MODE_PRIVATE).getString("personId", "");
            findViewById(R.id.button_email).setVisibility(View.GONE);
        }

        Call<User> call = apiService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                fillUserDetails();
                setupMailButton();
                Log.d(TAG, "GET user request successful");
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    private void fillUserDetails() {
        setText(R.id.show_name, user.getName());
        setText(R.id.show_surname, user.getSurname());
        setText(R.id.show_email, user.getEmail());
        Picasso.with(this).load(user.getPhotoUrl()).into((ImageView) findViewById(R.id.user_pic));
    }

    private void setText(int rId, String text) {
        ((TextView) findViewById(rId)).setText(text);
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