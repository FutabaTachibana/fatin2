package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * You can use this class to generate WebSocket request JSON strings. <br>
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
     * Builder class for constructing WebSocket request JSON strings. <br>
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
         * Sets the group_id field.
         * @param groupId the group identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder groupId(long groupId) {
            this.fields.addProperty("group_id", groupId);
            return this;
        }
        /**
         * Sets the user_id field.
         * @param userId the user identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder userId(long userId) {
            this.fields.addProperty("user_id", userId);
            return this;
        }
        public RequestBuilder messageId(long messageId) {
            this.fields.addProperty("message_id", messageId);
            return this;
        }
        /**
         * Sets the message field.
         * @param message the message content
         * @return this builder instance for method chaining
         */
        public RequestBuilder message(JsonArray message) {
            this.fields.add("message", message);
            return this;
        }
        /**
         * Sets the messages field.
         * @param messages the message content
         * @return this builder instance for method chaining
         */
        public RequestBuilder messages(JsonArray messages) {
            this.fields.add("messages", messages);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param flag the flag markup request
         * @return this builder instance for method chaining
         */
        public RequestBuilder flag(String flag) {
            this.fields.addProperty("flag", flag);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param approve if you approve the request
         * @return this builder instance for method chaining
         */
        public RequestBuilder approve(boolean approve) {
            this.fields.addProperty("approve", approve);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param reason the reason for declining
         * @return this builder instance for method chaining
         */
        public RequestBuilder reason(String reason) {
            this.fields.addProperty("reason", reason);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param remark remark after adding friend
         * @return this builder instance for method chaining
         */
        public RequestBuilder remark(String remark) {
            this.fields.addProperty("remark", remark);
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
