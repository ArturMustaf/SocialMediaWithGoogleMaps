package com.mustaf.printOnMaps;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PrintProjectApiService {
    @GET("/print-projects")
    Call<List<PrintProject>> getAllPrintProjects();
}
