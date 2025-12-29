package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 一个 Onebot 通知对象，表示接收到的一条通知，是一种 {@link AbstractOnebotNotice}，
 * 通过 {@code sub_type} 字段区分不同的通知子类型。
 * <ul>
 *     <li>{@code poke}: {@link PokeOnebotNotify}</li>
 *     <li>{@code lucky_king}: {@link LuckyKingOnebotNotify}</li>
 *     <li>{@code honor}: {@link HonorOnebotNotify}</li>
 * </ul>
 */
public interface AbstractOnebotNotify extends AbstractOnebotNotice {
    @SerializedName("sub_type") String subType();
}
