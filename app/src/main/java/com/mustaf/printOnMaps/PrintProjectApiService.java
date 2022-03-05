package com.mustaf.printOnMaps;

import com.mustaf.printOnMaps.model.Address;
import com.mustaf.printOnMaps.model.PrintProject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PrintProjectApiService {
    @GET("/print-projects")
    Call<List<PrintProject>> getAllPrintProjects();
    @GET("/addresses")
    Call<List<Address>> getAllAddresses();
    @POST("/location-data")
    Call<Address> createAddress(@Body Address address);
}
