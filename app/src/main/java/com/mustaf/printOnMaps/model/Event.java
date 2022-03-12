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

    @SerializedName("name")
    String name;
    @SerializedName("address")
    String address;
    @SerializedName("lat")
    double lat;
    @SerializedName("lng")
    double lng;
    @SerializedName("startDate")
    String startDate;
    @SerializedName("startTimeHour")
    int startTimeHour;
    @SerializedName("startTimeMinute")
    int startTimeMinute;
    @SerializedName("endDate")
    String endDate;
    @SerializedName("endTimeHour")
    int endTimeHour;
    @SerializedName("endTimeMinute")
    int endTimeMinute;
    @SerializedName("personId")
    String personId;
}
