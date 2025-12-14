package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public interface AbstractOnebotNotify extends AbstractOnebotNotice {
    // poke | lucky_king | honor
    @SerializedName("sub_type")
    String subType();
}
