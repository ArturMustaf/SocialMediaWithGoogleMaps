package com.mustaf.socialmaps;

import com.mustaf.socialmaps.model.Event;
import com.mustaf.socialmaps.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/events")
    Call<List<Event>> getAllEvents();
    @POST("/events")
    Call<Event> createEvent(@Body Event event);
    @POST("/users")
    Call<User> createUser(@Body User user);
    @GET("/users/{userId}")
    Call<User> getUserById(@Path("userId") String userId);
}
