package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

public interface AbstractOnebotNotify extends AbstractOnebotNotice {
    // poke | lucky_king | honor
    @SerializedName("sub_type")
    String subType();
}
