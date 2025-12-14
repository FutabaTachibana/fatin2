package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;

public record Status(
        @SerializedName("online") Boolean online,
        @SerializedName("good") Boolean good
) { }
