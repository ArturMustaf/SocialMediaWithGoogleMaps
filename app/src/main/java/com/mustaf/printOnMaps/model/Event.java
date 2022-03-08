package com.mustaf.printOnMaps.model;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Event {

    @SerializedName("address")
    String address;
    @SerializedName("lat")
    double lat;
    @SerializedName("lng")
    double lng;
    @SerializedName("personId")
    LocalDateTime startDate;
    @SerializedName("personId")
    LocalDateTime endDate;
    @SerializedName("personId")
    String personId;
}
