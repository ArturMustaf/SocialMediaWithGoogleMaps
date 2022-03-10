package com.mustaf.printOnMaps;

import com.mustaf.printOnMaps.model.Event;
import com.mustaf.printOnMaps.model.PrintProject;
import com.mustaf.printOnMaps.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/print-projects")
    Call<List<PrintProject>> getAllPrintProjects();
    @GET("/events")
    Call<List<Event>> getAllEvents();
    @POST("/events")
    Call<Event> createEvent(@Body Event event);
    @POST("/users")
    Call<User> createUser(@Body User user);
    @GET("/users/{userId}")
    Call<User> getUserById(@Path("userId") String userId);
}
