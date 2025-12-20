package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.type.Faces;
import org.f14a.fatin2.type.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generator for constructing Message arrays in JSON format.<br>
 * A Message is an array of segments, where each segment contains:
 * <ul>
 * <li>"type": String </li>
 * <li>"data": Map<String, String> </li>
 * </ul>
 * You can use this class to generate WebSocket request JSON strings.
 * It uses the Builder pattern to allow flexible and readable JSON construction.
 * <Blockquote><pre>
 * String message = MessageGenerator.builder()
 *         .segment("text").data("text", "你好").end()
 *         // Or use `text("你好")` directly
 *         .build();
 * </pre></Blockquote>
 * You can also use static methods for simple segments:
 * <Blockquote><pre>
 * String message = MessageGenerator.create(
 *         MessageGenerator.at(114514L),
 *         MessageGenerator.text("你好")
 * );
 * </pre></Blockquote>
 */
public class MessageGenerator {
    private static final Gson gson = new Gson();
    /**
     * Builder class for constructing a Message array.
     * Uses a fluent API to add message segments.
     */
    public static class MessageBuilder {
        private final JsonArray segments;
        /**
         * Constructor initializes an empty segments list.
         */
        public MessageBuilder() {
            this.segments = new JsonArray();
        }
        /**
         * Adds a message segment with the specified type and data.
         * @param type the segment type
         * @param data the segment data map
         * @return this builder instance for method chaining
         */
        public MessageBuilder addSegment(String type, Map<String, Object> data) {
            Map<String, Object> segment = new HashMap<>();
            segment.put("type", type);
            segment.put("data", data);
            this.segments.add(gson.toJsonTree(segment));
            return this;
        }
        public MessageBuilder addSegment(JsonObject message) {
            this.segments.add(message);
            return this;
        }
        /**
         * Starts building a custom segment with the specified type.
         * Returns a SegmentBuilder for adding data fields.
         * @param type the segment type
         * @return a SegmentBuilder instance
         */
        public SegmentBuilder segment(String type) {
            return new SegmentBuilder(this, type);
        }
        public MessageBuilder text(String text) {
            Map<String, Object> data = new HashMap<>();
            data.put("text", text);
            return addSegment("text", data);
        }
        public MessageBuilder at(long userId) {
            Map<String, Object> data = new HashMap<>();
            data.put("qq", Long.toString(userId));
            return addSegment("at", data);
        }
        public MessageBuilder face(Faces faces){
            Map<String, Object> data = new HashMap<>();
            data.put("id", Integer.toString(faces.slot()));
            return addSegment("face", data);
        }
        public MessageBuilder reply(long messageId){
            Map<String, Object> data = new HashMap<>();
            data.put("id", Long.toString(messageId));
            return addSegment("reply", data);
        }
        /**
         * Builds and returns the JSON string representation of the message array.
         * @return JSON array of message segments
         */
        public JsonArray build() {
            return this.segments;
        }
        /**
         * Returns the segments list (for testing or advanced usage).
         * @return JSON array of message segments
         */
        public JsonArray getSegments() {
            return this.segments;
        }
    }

    /**
     * Helper builder for constructing a custom segment with multiple data fields.
     */
    public static class SegmentBuilder {
        private final MessageBuilder parent;
        private final String type;
        private final Map<String, Object> data;
        /**
         * Constructor for SegmentBuilder.
         * @param parent the parent MessageBuilder
         * @param type the segment type
         */
        SegmentBuilder(MessageBuilder parent, String type) {
            this.parent = parent;
            this.type = type;
            this.data = new HashMap<>();
        }
        /**
         * Adds a data field to this segment.
         * @param key the field name
         * @param value the field value
         * @return this SegmentBuilder for method chaining
         */
        public SegmentBuilder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }
        /**
         * Completes this segment and returns to the parent MessageBuilder.
         * @return the parent MessageBuilder
         */
        public MessageBuilder end() {
            return parent.addSegment(type, data);
        }
    }

    /**
     * Creates a new MessageBuilder instance.
     * @return a new builder instance
     */
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    /**
     * Creates a message array JSON string from the given segments.
     * It is much simpler to use the Builder for complex messages.
     * <Blockquote><pre>
     * String message = MessageGenerator.create(
     *         MessageGenerator.at(114514L),
     *         MessageGenerator.text("你好")
     * );
     * </pre></Blockquote>
     * Available static methods:
     * <ui>
     * <li> text(String text)
     * <li> at(long userId)
     * <li> face(Faces faces)
     * <li> reply(long messageId)
     * </ui>
     * @param messages a series of message some static methods return, e.g. MessageGenerator.text("Hello")
     * @return JSON formatted string
     */
    public static JsonArray create(JsonObject ... messages) {
        JsonArray arr = new JsonArray();
        for (JsonObject msg : messages) {
            arr.add(msg);
        }
        return arr;
    }
    public static JsonArray create(Message ... messages) {
        JsonArray arr = new JsonArray();
        for (Message msg : messages) {
            arr.add(gson.toJsonTree(msg));
        }
        return arr;
    }
    public static JsonObject text(String text) {
        return gson.toJsonTree(Map.of(
                "type", "text",
                "data", Map.of("text", text)
        )).getAsJsonObject();
    }
    public static JsonObject at(long userId) {
        return gson.toJsonTree(Map.of(
                "type", "at",
                "data", Map.of("qq", Long.toString(userId))
        )).getAsJsonObject();
    }
    public static JsonObject face(Faces faces){
        return gson.toJsonTree(Map.of(
                "type", "face",
                "data", Map.of("id", Integer.toString(faces.slot()))
        )).getAsJsonObject();
    }
    public static JsonObject reply(long messageId) {
        return gson.toJsonTree(Map.of(
                "type", "reply",
                "data", Map.of("id", Long.toString(messageId))
        )).getAsJsonObject();
    }
}
