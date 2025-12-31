package org.f14a.fatin2.api.generator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.f14a.fatin2.api.sender.RequestSender;
import org.f14a.fatin2.model.Faces;

/**
 * 这个类通过 Builder 模式生成发送给 WebSocket 的请求内容。
 * <Blockquote><pre>
 * RequestGenerator.builder().userId(114514L).message(
 *         MessageGenerator.builder()
 *                 .at(114514L).text("你好")
 *                 .build()
 * ).build();
 * </pre></Blockquote>
 * @see RequestBuilder
 */
public final class RequestGenerator {
    private static final Gson GSON = new Gson();

    /**
     * 这个类使用 Builder 模式构建请求内容。
     * <Blockquote><pre>
     * RequestGenerator.builder().userId(114514L).message(
     *         MessageGenerator.builder()
     *                 .at(114514L).text("你好")
     *                 .build()
     * ).build();
     * </pre></Blockquote>
     * 在 Onebot v11 协议中，发送消息、修改昵称等主动操作均通过向客户端发送 WebSocket 请求实现。
     * <p>
     * 每种请求都有其特定的字段要求，详见
     * <a href="https://github.com/botuniverse/onebot-11/blob/master/api/public.md">Onebot API v11 文档</a>
     * 或 <a href="https://napcat.apifox.cn/">NapCat 接口文档</a>。
     * <p>
     * 常用的操作已经被整合进 {@link RequestSender} 类中，如果你没有特别的需求，通常不需要直接使用此类。
     */
    public final static class RequestBuilder {
        private final JsonObject fields;

        /**
         * @see RequestBuilder
         */
        public RequestBuilder() {
            this.fields = new JsonObject();
        }

        /**
         * 用于设置请求部分的键值对。
         * @param key 键
         * @param value 值
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder add(String key, String value) {
            this.fields.addProperty(key, value);
            return this;
        }
        
        /**
         * 用于设置请求部分的键值对。
         * @param key 键
         * @param value 值
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder add(String key, JsonElement value) {
            this.fields.add(key, value);
            return this;
        }
        
        /**
         * 设置 {@code group_id} 的值。
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder groupId(long groupId) {
            this.fields.addProperty("group_id", groupId);
            return this;
        }

        /**
         * 设置 {@code user_id} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder userId(long userId) {
            this.fields.addProperty("user_id", userId);
            return this;
        }

        /**
         * 设置 {@code message_id} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder messageId(int messageId) {
            this.fields.addProperty("message_id", messageId);
            return this;
        }

        /**
         * 设置 {@code message} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder message(JsonArray message) {
            this.fields.add("message", message);
            return this;
        }

        /**
         * 设置 {@code messages} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder messages(JsonArray messages) {
            this.fields.add("messages", messages);
            return this;
        }

        /**
         * 设置 {@code enable} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder enable(boolean enable) {
            this.fields.addProperty("enable", enable);
            return this;
        }

        /**
         * 设置 {@code special_title} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder specialTitle(String specialTitle) {
            this.fields.addProperty("special_title", specialTitle);
            return this;
        }

        /**
         * 设置 {@code reject_add_request} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder rejectAddRequest(boolean rejectAddRequest) {
            this.fields.addProperty("reject_add_request", rejectAddRequest);
            return this;
        }

        /**
         * 设置 {@code duration} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder duration(int durationSeconds) {
            this.fields.addProperty("duration", durationSeconds);
            return this;
        }

        /**
         * 设置 {@code flag} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder flag(String flag) {
            this.fields.addProperty("flag", flag);
            return this;
        }

        /**
         * 设置 {@code approve} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder approve(boolean approve) {
            this.fields.addProperty("approve", approve);
            return this;
        }

        /**
         * 设置 {@code reason} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder reason(String reason) {
            this.fields.addProperty("reason", reason);
            return this;
        }

        /**
         * 设置 {@code remark} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder remark(String remark) {
            this.fields.addProperty("remark", remark);
            return this;
        }

        /**
         * 设置 {@code emoji_id} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder emojiId(Faces emojiId) {
            this.fields.addProperty("emoji_id", emojiId.slot());
            return this;
        }

        /**
         * 设置 {@code emoji_id} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder emojiId(int emojiId) {
            this.fields.addProperty("emoji_id", emojiId);
            return this;
        }

        /**
         * 设置 {@code set} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder set(boolean set) {
            this.fields.addProperty("set", set);
            return this;
        }

        /**
         * 设置 {@code group_name} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder groupName(String groupName) {
            this.fields.addProperty("group_name", groupName);
            return this;
        }

        /**
         * 设置 {@code card} 的值.
         * @return 当前的 {@link RequestBuilder} 实例
         */
        public RequestBuilder card(String card) {
            this.fields.addProperty("card", card);
            return this;
        }

        /**
         * 完成请求构建并返回 {@link JsonObject}。
         * @return 构建完成的请求
         */
        public JsonObject build() {
            return this.fields;
        }
    }

    /**
     * @see RequestBuilder
     */
    public static RequestBuilder builder() {
        return new RequestBuilder();
    }
}
