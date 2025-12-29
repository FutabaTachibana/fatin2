package org.f14a.fatin2.model.message;

import com.google.gson.annotations.SerializedName;

/**
 * 发送消息的用户信息。
 * @param userId 发送消息的用户 ID
 * @param nickname 发送消息的用户昵称
 * @param sex 发送消息的用户性别，如"male""female""unknown"
 * @param age 发送消息的用户年龄
 * @param card 发送消息的用户群名片
 * @param role 发送消息的用户在群中的角色
 * @see OnebotMessage
 */
public record Sender (
    @SerializedName("user_id")  long   userId,
    @SerializedName("nickname") String nickname,
    @SerializedName("sex")      String sex,
    @SerializedName("age")      int    age,
    @SerializedName("card")     String card,
    @SerializedName("role")     String role
) {}
