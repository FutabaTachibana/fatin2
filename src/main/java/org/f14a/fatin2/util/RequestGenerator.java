package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.Map;

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
    private static final Gson gson = new Gson();
    /**
     * Builder class for constructing WebSocket request JSON strings. <br>
     * Uses the Builder pattern to allow flexible and readable JSON construction.
     */
    public static class RequestBuilder {
        private final Map<String, Object> fields;
        /**
         * Constructor initializes an empty fields map.
         */
        public RequestBuilder() {
            this.fields = new LinkedHashMap<>();
        }
        /**
         * Sets the group_id field.
         * @param groupId the group identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder groupId(Long groupId) {
            this.fields.put("group_id", groupId);
            return this;
        }
        /**
         * Sets the user_id field.
         * @param userId the user identifier
         * @return this builder instance for method chaining
         */
        public RequestBuilder userId(Long userId) {
            this.fields.put("user_id", userId);
            return this;
        }
        /**
         * Sets the message field.
         * @param message the message content
         * @return this builder instance for method chaining
         */
        public RequestBuilder message(JsonElement message) {
            this.fields.put("message", message);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param flag the flag markup request
         * @return this builder instance for method chaining
         */
        public RequestBuilder flag(String flag) {
            this.fields.put("flag", flag);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param approve if you approve the request
         * @return this builder instance for method chaining
         */
        public RequestBuilder approve(boolean approve) {
            this.fields.put("approve", approve);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param reason the reason for declining
         * @return this builder instance for method chaining
         */
        public RequestBuilder reason(String reason) {
            this.fields.put("reason", reason);
            return this;
        }
        /**
         * Adds a custom field with any type of value.
         * @param remark remark after adding friend
         * @return this builder instance for method chaining
         */
        public RequestBuilder remark(String remark) {
            this.fields.put("remark", remark);
            return this;
        }

        /**
         * Builds and returns the JSON string representation of the request.
         * @return JSON formatted string
         */
        public String build() {
            return gson.toJson(fields);
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
