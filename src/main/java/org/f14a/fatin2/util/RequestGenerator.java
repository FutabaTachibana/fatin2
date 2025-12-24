package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.f14a.fatin2.type.Faces;

/**
 * You can use this class to generate WebSocket request JSON strings.
 * <p>
 * It uses the Builder pattern to allow flexible and readable JSON construction.
 * <Blockquote><pre>
 * RequestGenerator.builder().userId(114514L).message(
 *         MessageGenerator.builder()
 *                 .segment("text").data("text", "你好").end()
 *                 .build()
 * ).build();
 * </pre></Blockquote>
 */
public class RequestGenerator {
    private static final Gson GSON = new Gson();
    /**
     * Builder class for constructing WebSocket request JSON strings.
     * <p>
     * Uses the Builder pattern to allow flexible and readable JSON construction.
     */
    public static class RequestBuilder {
        private final JsonObject fields;
        /**
         * Constructor initializes an empty fields map.
         */
        public RequestBuilder() {
            this.fields = new JsonObject();
        }
        public RequestBuilder add(String key, String value) {
            this.fields.addProperty(key, value);
            return this;
        }
        public RequestBuilder add(String key, JsonElement value) {
            this.fields.add(key, value);
            return this;
        }
        /**
         * Sets the <code>group_id</code> field.
         * @param groupId the group identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder groupId(long groupId) {
            this.fields.addProperty("group_id", groupId);
            return this;
        }
        /**
         * Sets the <code>user_id</code> field.
         * @param userId the user identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder userId(long userId) {
            this.fields.addProperty("user_id", userId);
            return this;
        }
        /**
         * Sets the <code>message_id</code> field.
         * @param messageId the message identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder messageId(int messageId) {
            this.fields.addProperty("message_id", messageId);
            return this;
        }
        /**
         * Sets the <code>message</code> field.
         * @param message the message content
         * @return this builder instance for method chaining
         */
        public RequestBuilder message(JsonArray message) {
            this.fields.add("message", message);
            return this;
        }
        /**
         * Sets the <code>messages</code> field.
         * @param messages the messages content
         * @return this builder instance for method chaining
         */
        public RequestBuilder messages(JsonArray messages) {
            this.fields.add("messages", messages);
            return this;
        }
        /**
         * Sets the <code>enable</code> field.
         * @param enable whether to enable (something)
         * @return this builder instance for method chaining
         */
        public RequestBuilder enable(boolean enable) {
            this.fields.addProperty("enable", enable);
            return this;
        }
        /**
         * Sets the <code>special_title</code> field.
         * @param specialTitle the special title to set
         * @return this builder instance for method chaining
         */
        public RequestBuilder specialTitle(String specialTitle) {
            this.fields.addProperty("special_title", specialTitle);
            return this;
        }
        /**
         * Sets the <code>reject_add_request</code> field.
         * @param rejectAddRequest whether to reject the add request
         * @return this builder instance for method chaining
         */
        public RequestBuilder rejectAddRequest(boolean rejectAddRequest) {
            this.fields.addProperty("reject_add_request", rejectAddRequest);
            return this;
        }
        /**
         * Sets the <code>duration</code> field.
         * @param durationSeconds the duration in seconds
         * @return this builder instance for method chaining
         */
        public RequestBuilder duration(int durationSeconds) {
            this.fields.addProperty("duration", durationSeconds);
            return this;
        }
        /**
         * Sets the <code>flag</code> field.
         * @param flag the flag markup request
         * @return this builder instance for method chaining
         */
        public RequestBuilder flag(String flag) {
            this.fields.addProperty("flag", flag);
            return this;
        }
        /**
         * Sets the <code>approve</code> field.
         * @param approve if you approve the request
         * @return this builder instance for method chaining
         */
        public RequestBuilder approve(boolean approve) {
            this.fields.addProperty("approve", approve);
            return this;
        }
        /**
         * Sets the <code>reason</code> field.
         * @param reason the reason for declining
         * @return this builder instance for method chaining
         */
        public RequestBuilder reason(String reason) {
            this.fields.addProperty("reason", reason);
            return this;
        }
        /**
         * Sets the <code>remark</code> field.
         * @param remark remark after adding friend
         * @return this builder instance for method chaining
         */
        public RequestBuilder remark(String remark) {
            this.fields.addProperty("remark", remark);
            return this;
        }
        /**
         * Sets the <code>emoji_id</code> field.
         * @param emojiId the emoji identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder emojiId(Faces emojiId) {
            this.fields.addProperty("emoji_id", emojiId.slot());
            return this;
        }
        /**
         * Sets the <code>emoji_id</code> field.
         * @param emojiId the emoji identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder emojiId(int emojiId) {
            this.fields.addProperty("emoji_id", emojiId);
            return this;
        }
        /**
         * Sets the <code>set</code> field.
         * @param set whether to set (something)
         * @return this builder instance for method chaining
         */
        public RequestBuilder set(boolean set) {
            this.fields.addProperty("set", set);
            return this;
        }
        /**
         * Sets the <code>group_name</code> field.
         * @param groupName the group name
         * @return this builder instance for method chaining
         */
        public RequestBuilder groupName(String groupName) {
            this.fields.addProperty("group_name", groupName);
            return this;
        }
        /**
         * Sets the <code>card</code> field.
         * @param card the card name
         * @return this builder instance for method chaining
         */
        public RequestBuilder card(String card) {
            this.fields.addProperty("card", card);
            return this;
        }

        /**
         * Builds and returns the JSON string representation of the request.
         * @return JSON formatted string
         */
        public JsonObject build() {
            return this.fields;
        }
    }

    /**
     * Starts building a new WebSocket request.
     * @return a RequestBuilder instance
     */
    public static RequestBuilder builder() {
        return new RequestBuilder();
    }
}
