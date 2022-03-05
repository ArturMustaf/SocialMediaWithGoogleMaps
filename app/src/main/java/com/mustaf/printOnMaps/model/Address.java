package com.mustaf.printOnMaps.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Address {

    @SerializedName("address")
    String address;

}
