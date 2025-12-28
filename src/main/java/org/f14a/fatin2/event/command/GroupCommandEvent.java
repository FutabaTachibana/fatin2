package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.command.parse.CommandParseResult;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.model.message.GroupOnebotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupCommandEvent extends GroupMessageEvent implements CommandEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupCommandEvent.class);
    private final CommandParseResult result;

    public static GroupMessageEvent getCommandOrBasic(GroupOnebotMessage message) {
        CommandParseResult result = CommandParseResult.of(message.selfId(), message.messages());
        if (result.isCommand()) {
            LOGGER.debug("GroupCommandEvent: Command parsed: {}", result.rawCommandLine());
            return new GroupCommandEvent(message, result);
        }
        return new GroupMessageEvent(message);
    }
    public GroupCommandEvent(GroupOnebotMessage message, CommandParseResult result) {
        super(message);
        this.result = result;
    }

    @Override
    public CommandParseResult getResult() {
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
