package org.f14a.fatin2.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 表示一个响应对象。
 * @param status "ok"
 * @param retcode 成功为 0
 * @param data 响应数据，具体内容根据响应的请求而定
 * @param message 不清楚是干啥滴
 * @param wording go-cqhttp字段，错误信息
 * @param echo 请求的 {@code echo} 值，用于追踪响应
 */
public record Response(
    @SerializedName("status")  String      status,
    @SerializedName("retcode") int         retcode,
    @SerializedName("data")    JsonElement data,
    @SerializedName("message") String      message,
    @SerializedName("wording") String      wording,
    @SerializedName("echo")    String      echo
) {
    public boolean isSuccess() {
        return "ok".equalsIgnoreCase(this.status) && this.retcode == 0;
    }
    public int getMessageId() {
        if (this.data instanceof JsonObject dataObject && dataObject.has("message_id")) {
            return dataObject.get("message_id").getAsInt();
        }
        return 0;
    }
}
