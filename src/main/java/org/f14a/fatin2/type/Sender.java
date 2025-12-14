package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;

public record Sender (
    @SerializedName("user_id") Long userId,
    @SerializedName("nickname") String nickname,
    // male | female
    @SerializedName("sex") String sex,
    @SerializedName("age") Integer age,
    @SerializedName("card") String card,
    @SerializedName("role") String role
) {}
