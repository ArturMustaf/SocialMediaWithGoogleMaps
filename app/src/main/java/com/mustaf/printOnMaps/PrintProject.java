package com.mustaf.printOnMaps;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrintProject {
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("sourceUrl")
    public String sourceUrl;
}
