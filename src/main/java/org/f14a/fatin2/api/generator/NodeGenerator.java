package org.f14a.fatin2.api.generator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.api.sender.MessageSender;

/**
 * 这个类用于构造伪造消息聊天记录中的节点。
 * <p>
 * 对于一般的伪造消息聊天记录，可以使用 {@link MessageSender#sendPrivateForward(long, long, String, JsonArray)}
 * 或 {@link MessageSender#sendGroupForward(long, long, String, JsonArray)} 方法直接发送。
 * <p>
 * 如果不知道怎么使用这个类，可以看上述两个方法的实现。
 */
public final class NodeGenerator {
    private static Gson GSON = new Gson();

    /**
     * @see NodeGenerator
     */
    public final static class NodeBuilder {
        private final JsonObject node;

        /**
         * @see NodeGenerator
         */
        public NodeBuilder() {
            this.node = new JsonObject();
        }

        /**
         * @see NodeGenerator
         */
        public NodeBuilder userId(long userId) {
            this.node.addProperty("user_id", String.valueOf(userId));
            return this;
        }

        /**
         * @see NodeGenerator
         */
        public NodeBuilder nickname(String nickname) {
            this.node.addProperty("nickname", nickname);
            return this;
        }

        /**
         * @see NodeGenerator
         */
        public NodeBuilder content(JsonArray content) {
            this.node.add("content", content);
            return this;
        }

        /**
         * @see NodeGenerator
         */
        public NodeBuilder content(JsonObject ... content) {
            this.node.add("content", MessageGenerator.create(content));
            return this;
        }

        /**
         * @see NodeGenerator
         */
        public JsonObject build() {
            return this.node;
        }
    }

    /**
     * @see NodeGenerator
     */
    public static NodeBuilder builder() {
        return new NodeBuilder();
    }
}
