package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;

public record Sender (
    // User ID
    @SerializedName("user_id")
    Long userId,
    // Nickname
    @SerializedName("nickname")
    String nickname,
    // Sex
    // male | female
    @SerializedName("sex") String sex,
    // Age
    @SerializedName("age")
    Integer age,
    // Card
    @SerializedName("card")
    String card,
    // Role
    @SerializedName("role")
    String role
) {}
