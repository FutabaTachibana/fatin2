package org.f14a.fatin2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Onebot 消息的抽象接口，定义了所有 Onebot 消息共有的字段。
 * <p>
 * <a href="https://github.com/botuniverse/onebot-11/blob/master/event/README.md">Onebot API v11 文档</a>
 * 介绍了所有类型的消息和这些字段的含义。
 * <p>
 * {@link AbstractOnebotMessage#postType()} 返回的值有五种:
 * <ul>
 *     <li>message / message_sent: 消息</li>
 *     <li>notice: 通知</li>
 *     <li>request: 请求</li>
 *     <li>meta_event: 元事件</li>
 * </ul>
 */
public interface AbstractOnebotMessage {
    @SerializedName("time")      long   time();
    @SerializedName("post_type") String postType();
    @SerializedName("self_id")   long   selfId();
}
