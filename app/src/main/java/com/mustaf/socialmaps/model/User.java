package com.mustaf.socialmaps.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @SerializedName("googleAccId")
    private String googleAccId;
    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("photoUrl")
    private String photoUrl;
    @SerializedName("email")
    private String email;
}
