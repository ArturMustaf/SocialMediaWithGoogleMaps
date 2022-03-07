package com.mustaf.printOnMaps.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Address {

    @SerializedName("address")
    String address;
    @SerializedName("lat")
    double lat;
    @SerializedName("lng")
    double lng;
    @SerializedName("personId")
    String personId;
}
