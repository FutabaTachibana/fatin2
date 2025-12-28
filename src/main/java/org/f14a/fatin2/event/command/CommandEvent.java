package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.command.parse.CommandParseResult;
import org.f14a.fatin2.event.message.IMessageEvent;
import org.f14a.fatin2.model.message.OnebotMessage;

public interface CommandEvent extends IMessageEvent {
    /**
     * @return 命令解析的 {@link CommandParseResult} 对象
     */
    CommandParseResult getResult();

    /**
     * @return 当前的命令名称，以区分不同的别名
     */
    default String getCommand() {
        return getResult().command();
    }

    /**
     * @return 命令参数，不包含图片、表情等非文本内容
     */
    default String[] getArgs() {
        return getResult().args();
    }

    /**
     * @return 获取触发指令时是否艾特了机器人自身
     */
    default boolean isAtBot() {
        return getResult().atBot();
    }

    /**
     * @return 获取触发指令时是否艾特了机器人自身
     */
    default boolean hasReply() {
        return getResult().hasReply();
    }
}
