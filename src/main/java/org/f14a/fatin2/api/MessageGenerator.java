package org.f14a.fatin2.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.f14a.fatin2.model.Faces;
import org.f14a.fatin2.model.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类提供了为 {@link MessageSender} 构建消息内容的功能。
 * <p>
 * 一个消息由若干消息段 {@link Message} 组成，每个消息段包含以下字段：
 * <ul>
 * <li>{@code "type": String}</li>
 * <li>{@code "data": Map<String, Object>}</li>
 * </ul>
 * 这个类提供了两种方法来构建消息内容：使用 Builder 模式和静态方法。
 * <p>
 * 你可以使用 {@link MessageBuilder} 类来构建复杂的消息内容。
 * <Blockquote><pre>
 * String message = MessageGenerator.builder()
 *         .segment("at").data("qq", "114514L").end()
 *         .segment("text").data("text", "你好").end()
 *         // 也可以直接使用 `.at(114514L).text("你好")`
 *         // 快速构建（适合简单消息段），两种方法等价
 *         .build();
 * </pre></Blockquote>
 * 你也可以使用静态方法快速创建简单的消息段。
 * <Blockquote><pre>
 * String message = MessageGenerator.create(
 *         MessageGenerator.at(114514L),
 *         MessageGenerator.text("你好")
 * );
 * </pre></Blockquote>
 */
public class MessageGenerator {
    private static final Gson GSON = new Gson();

    /**
     * 用于以 Builder 模式构建消息内容的类。
     * <p>
     * 使用 {@link #segment(String type)} 开始构建一个自定义消息段，
     * 或者直接使用 {@link #text(String text)} 等快捷方法构建常用消息段。
     * <Blockquote><pre>
     * String message = MessageGenerator.builder()
     *         .segment("at").data("qq", "114514L").end()
     *         .segment("text").data("text", "你好").end()
     *         // 也可以直接使用 `.at(114514L).text("你好")`
     *         // 快速构建（适合简单消息段），两种方法等价
     *         .build();
     * </pre></Blockquote>
     */
    public static class MessageBuilder {
        private final JsonArray segments;
        /**
         * @see MessageBuilder
         */
        public MessageBuilder() {
            this.segments = new JsonArray();
        }

        /**
         * 直接添加一个消息段。
         * @param type 消息段的 {@code "type"} 字段
         * @param data 消息段的 {@code "data"} 字段，接受一个 Map
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder addSegment(String type, Map<String, Object> data) {
            Map<String, Object> segment = new HashMap<>();
            segment.put("type", type);
            segment.put("data", data);
            this.segments.add(GSON.toJsonTree(segment));
            return this;
        }

        /**
         * 直接添加一个消息段。
         * @param type 消息段的 {@code "type"} 字段
         * @param data 消息段的 {@code "data"} 字段，接受一个 {@link JsonElement}
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder addSegment(String type, JsonElement data) {
            Map<String, Object> segment = new HashMap<>();
            segment.put("type", type);
            segment.put("data", data);
            this.segments.add(GSON.toJsonTree(segment));
            return this;
        }

        /**
         * 直接添加一个消息段。
         * @param message 一个 {@link JsonElement}
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder addSegment(JsonElement message) {
            this.segments.add(message);
            return this;
        }

        /**
         * 开始构造一个自定义消息段。
         * @param type 消息段的 {@code "type"} 字段
         * @return 一个 {@link SegmentBuilder} 实例，用于添加数据字段
         */
        public SegmentBuilder segment(String type) {
            return new SegmentBuilder(this, type);
        }

        /**
         * 快速构建文本消息段，等价于 <code>segment("text").data("text", text).end()</code>。
         * @param text 文本内容
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder text(String text) {
            return addSegment("text", Map.of("text", text));
        }

        /**
         * 快速构建 @ 消息段，等价于 <code>segment("at").data("qq", text).end()</code>。
         * @param userId 要艾特的用户的 ID
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder at(long userId) {
            return addSegment("at", Map.of("qq", Long.toString(userId)));
        }

        /**
         * 快速构建表情消息段，等价于 <code>segment("face").data("id", faceId).end()</code>。
         * @param faces {@link Faces} 的值
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder face(Faces faces){
            return addSegment("face", Map.of("id", Integer.toString(faces.slot())));
        }

        /**
         * 快速构造回复消息段，等价于 <code>segment("reply").data("id", messageId).end()</code>。
         * <p>
         * 必须是一个消息段的第一个元素。
         * @param messageId 消息 ID
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder reply(int messageId){
            return addSegment("reply", Map.of("id", Long.toString(messageId)));
        }

        /**
         * 快速构建图片消息段，等价于 <code>segment("image").data("file", file).end()</code>。
         * @param file 图片的 URL 或路径 <br>
         *             e.g. <b>"http://..."</b>, <b>"file://..."</b>, <b>"base64://..."</b>
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder image(String file){
            return addSegment("image", Map.of("file", file));
        }

        /**
         * 快速构建文件消息段，等价于 <code>segment("reply").data("file", file).data("name", name).end()</code>。
         * @param file 文件的 URL 或路径 <br>
         *             e.g. <b>"http://..."</b>, <b>"file://..."</b>, <b>"base64://..."</b>
         * @param name 文件名
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder file(String file, String name){
            return addSegment("file", Map.of("file", file, "name", name));
        }

        /**
         * 快速构建伪造转发聊天记录。
         * @param node 由 {@link NodeGenerator} 构建
         * @return 当前的 {@link MessageBuilder} 实例
         */
        public MessageBuilder node(JsonObject node) {
            return addSegment("node", node);
        }

        /**
         * 完成消息构建并返回 {@link JsonArray}。
         * @return 构建完成的消息
         */
        public JsonArray build() {
            return this.segments;
        }
    }

    /**
     * 用于构建复杂的单个消息段的类。
     * <p>
     * 使用 {@link MessageBuilder#segment(String type)} 方法创建。
     * <p>
     * 使用 {@link SegmentBuilder#end()} 结束创建。
     */
    public static class SegmentBuilder {
        private final MessageBuilder parent;
        private final String type;
        private final Map<String, Object> data;

        /**
         * 构造 {@link SegmentBuilder} 实例。
         * @param parent 调用者的 {@link MessageBuilder} 实例
         * @param type 消息段的 {@code "type"} 字段
         */
        SegmentBuilder(MessageBuilder parent, String type) {
            this.parent = parent;
            this.type = type;
            this.data = new HashMap<>();
        }

        /**
         * 向消息段的 {@code "data"} 字段添加一个键值对。
         * @param key 键
         * @param value 值
         * @return 当前的 {@link SegmentBuilder} 实例
         */
        public SegmentBuilder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        /**
         * 完成消息段的构建。
         * @return 调用它的 {@link MessageBuilder} 实例
         */
        public MessageBuilder end() {
            return parent.addSegment(type, data);
        }
    }

    /**
     * @see MessageBuilder
     */
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    /**
     * 快速创建一个消息内容，不需要构造任何实例。
     * <Blockquote><pre>
     * String message = MessageGenerator.create(
     *         MessageGenerator.at(114514L),
     *         MessageGenerator.text("你好")
     * );
     * </pre></Blockquote>
     * 支持的消息段有：
     * <ul>
     * <li> text(String text)
     * <li> at(long userId)
     * <li> face(Faces faces)
     * <li> reply(long messageId)
     * <li> image(String file)
     * <li> file(String file, String name)
     * </ul>
     * 较复杂的消息段仍需要使用 {@link MessageBuilder} 来构建。
     * @param messages 一系列上述<b>静态方法</b>返回的 {@link JsonObject} 消息段
     * @return 一个与 Builder 模式等价的 {@link JsonArray} 消息内容
     */
    public static JsonArray create(JsonObject ... messages) {
        JsonArray arr = new JsonArray();
        for (JsonObject msg : messages) {
            arr.add(msg);
        }
        return arr;
    }

    /**
     * 快速创建一个消息内容，不需要构造任何实例。
     * <Blockquote><pre>
     * String message = MessageGenerator.create(
     *         MessageGenerator.at(114514L),
     *         MessageGenerator.text("你好")
     * );
     * </pre></Blockquote>
     * 支持的消息段有：
     * <ul>
     * <li> text(String text)
     * <li> at(long userId)
     * <li> face(Faces faces)
     * <li> reply(long messageId)
     * <li> image(String file)
     * <li> file(String file, String name)
     * </ul>
     * 较复杂的消息段仍需要使用 {@link MessageBuilder} 来构建。
     * @param messages 一系列 {@link Message} 的实例。
     * @return 一个与 Builder 模式等价的 {@link JsonArray} 消息内容
     */
    public static JsonArray create(Message ... messages) {
        JsonArray arr = new JsonArray();
        for (Message msg : messages) {
            arr.add(GSON.toJsonTree(msg));
        }
        return arr;
    }

    /**
     * 快速创建一个文本消息段。
     * @param text 文本内容
     * @return 一个 {@link JsonObject} 消息段
     * @see #create(JsonObject... messages)
     */
    public static JsonObject text(String text) {
        return GSON.toJsonTree(Map.of(
                "type", "text",
                "data", Map.of("text", text)
        )).getAsJsonObject();
    }

    /**
     * 快速创建一个 @ 消息段。
     * @param userId 艾特的用户的 ID
     * @return 一个 {@link JsonObject} 消息段
     * @see #create(JsonObject... messages)
     */
    public static JsonObject at(long userId) {
        return GSON.toJsonTree(Map.of(
                "type", "at",
                "data", Map.of("qq", Long.toString(userId))
        )).getAsJsonObject();
    }

    /**
     * 快速创建一个表情消息段。
     * @param faces {@link Faces} 的值
     * @return 一个 {@link JsonObject} 消息段
     * @see #create(JsonObject... messages)
     */
    public static JsonObject face(Faces faces){
        return GSON.toJsonTree(Map.of(
                "type", "face",
                "data", Map.of("id", Integer.toString(faces.slot()))
        )).getAsJsonObject();
    }

    /**
     * 快速创建一个回复消息段。
     * <p>
     * 必须是一个消息段的第一个元素。
     * @param messageId 消息 ID
     * @return 一个 {@link JsonObject} 消息段
     * @see #create(JsonObject... messages)
     */
    public static JsonObject reply(int messageId) {
        return GSON.toJsonTree(Map.of(
                "type", "reply",
                "data", Map.of("id", Long.toString(messageId))
        )).getAsJsonObject();
    }

    /**
     * 快速创建一个图片消息段。
     * @param file 图片的 URL 或路径 <br>
     *             e.g. <b>"http://..."</b>, <b>"file://..."</b>, <b>"base64://..."</b>
     * @return 一个 {@link JsonObject} 消息段
     * @see #create(JsonObject... messages)
     */
    public static JsonObject image(String file){
        return GSON.toJsonTree(Map.of(
                "type", "image",
                "data", Map.of("file", file)
        )).getAsJsonObject();
    }

    /**
     * 快速创建一个文件消息段。
     * @param file 文件的 URL 或路径 <br>
     *             e.g. <b>"http://..."</b>, <b>"file://..."</b>, <b>"base64://..."</b>
     * @param name 文件名
     * @return 一个 {@link JsonObject} 消息段
     * @see #create(JsonObject... messages)
     */
    public static JsonObject file(String file, String name){
        return GSON.toJsonTree(Map.of(
                "type", "file",
                "data", Map.of("file", file, "name", name)
        )).getAsJsonObject();
    }
}
