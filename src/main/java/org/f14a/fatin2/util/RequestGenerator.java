package org.f14a.fatin2.util;

import com.google.gson.Gson;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* A class contains some helper methods to generate request HTTP bodies.
* */
public class RequestGenerator {
    private static final Gson gson = new Gson();
    /**
     * Builder class for constructing WebSocket request JSON strings.
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
        public RequestBuilder message(String message) {
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
     * Creates a new RequestBuilder instance.
     * <Blockquote><pre>
     * RequestGenerator.builder().userId(1925451275L).message(
     *         MessageGenerator.builder().segment("text")
     *                 .data("text", "你好")
     *                 .end()
     *                 .build()
     * ).build();
     * </pre></Blockquote>
     * @return a new builder instance
     */
    public static RequestBuilder builder() {
        return new RequestBuilder();
    }
}
