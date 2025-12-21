package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class NodeGenerator {
    private static Gson gson = new Gson();
    public static class NodeBuilder {
        private final JsonObject node;
        public NodeBuilder() {
            this.node = new JsonObject();
        }
        public NodeBuilder userId(long userId) {
            this.node.addProperty("user_id", String.valueOf(userId));
            return this;
        }
        public NodeBuilder nickname(String nickname) {
            this.node.addProperty("nickname", nickname);
            return this;
        }
        public NodeBuilder content(JsonArray content) {
            this.node.add("content", content);
            return this;
        }
        public NodeBuilder content(JsonObject ... content) {
            this.node.add("content", MessageGenerator.create(content));
            return this;
        }
        public JsonObject build() {
            return this.node;
        }
    }
    public static NodeBuilder builder() {
        return new NodeBuilder();
    }
}
