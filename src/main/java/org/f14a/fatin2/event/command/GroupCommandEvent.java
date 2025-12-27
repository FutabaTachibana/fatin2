package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.model.message.GroupOnebotMessage;

public class GroupCommandEvent extends GroupMessageEvent implements CommandEvent {
    private final CommandParser.Result result;

    public static GroupMessageEvent getCommandOrBasic(GroupOnebotMessage message) {
        CommandParser.Result result = CommandParser.parse(message.selfId(), message.messages());
        if (result.isCommand()) {
            return new GroupCommandEvent(message, result);
        }
        return new GroupMessageEvent(message);
    }
    public GroupCommandEvent(GroupOnebotMessage message, CommandParser.Result result) {
        super(message);
        this.result = result;
    }

    @Override
    public CommandParser.Result getResult() {
        return result;
    }
    @Override
    public String getCommand() {
        return result.command();
    }
    @Override
    public String[] getArgs() {
        return result.args();
    }
    @Override
    public boolean isAtBot() {
        return result.atBot();
    }
    @Override
    public boolean hasReply() {
        return result.hasReply();
    }
}
