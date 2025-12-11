package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;

public class Sender {
    // User ID
    @SerializedName("user_id")
    private int userId;

    // Nickname
    @SerializedName("nickname")
    private String nickname;

    // Sex
    // male | female
    @SerializedName("sex")
    private String sex;

    // Age
    @SerializedName("age")
    private int age;

    // Card
    @SerializedName("card")
    private String card;

    // Role
    @SerializedName("role")
    private String role;

    // Getters
    public int getUserId() {
        return userId;
    }
    public String getNickname() {
        return nickname;
    }
    public String getSex() {
        return sex;
    }
    public int getAge() {
        return age;
    }
    public String getCard() {
        return card;
    }
    public String getRole() {
        return role;
    }
}
