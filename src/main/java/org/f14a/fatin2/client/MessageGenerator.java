package org.f14a.fatin2.client;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generator for constructing Message arrays in JSON format.
 * A Message is an array of segments, where each segment contains:
 * - "type": String
 * - "data": Map<String, String>
 */
public class MessageGenerator {
    private static final Gson gson = new Gson();
    /**
     * Builder class for constructing a Message array.
     * Uses a fluent API to add message segments.
     */
    public static class MessageBuilder {
        private final List<Map<String, Object>> segments;
        /**
         * Constructor initializes an empty segments list.
         */
        public MessageBuilder() {
            this.segments = new ArrayList<>();
        }
        /**
         * Adds a message segment with the specified type and data.
         * @param type the segment type
         * @param data the segment data map
         * @return this builder instance for method chaining
         */
        public MessageBuilder addSegment(String type, Map<String, String> data) {
            Map<String, Object> segment = new HashMap<>();
            segment.put("type", type);
            segment.put("data", data);
            this.segments.add(segment);
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
        /**
         * Builds and returns the JSON string representation of the message array.
         * @return JSON formatted string
         */
        public String build() {
            return gson.toJson(segments);
        }
        /**
         * Returns the segments list (for testing or advanced usage).
         * @return the list of segments
         */
        public List<Map<String, Object>> getSegments() {
            return segments;
        }
    }

    /**
     * Helper builder for constructing a custom segment with multiple data fields.
     */
    public static class SegmentBuilder {
        private final MessageBuilder parent;
        private final String type;
        private final Map<String, String> data;
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
        public SegmentBuilder data(String key, String value) {
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
}
