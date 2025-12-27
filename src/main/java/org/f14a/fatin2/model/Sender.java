package org.f14a.fatin2.model;

import com.google.gson.annotations.SerializedName;

public record Sender (
    @SerializedName("user_id") long userId,
    @SerializedName("nickname") String nickname,
    // male | female
    @SerializedName("sex") String sex,
    @SerializedName("age") int age,
    @SerializedName("card") String card,
    @SerializedName("role") String role
) {}
