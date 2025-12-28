package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.command.parse.CommandParseResult;
import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.model.message.PrivateOnebotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateCommandEvent extends PrivateMessageEvent implements CommandEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateCommandEvent.class);
    private final CommandParseResult result;

    public static PrivateMessageEvent getCommandOrBasic(PrivateOnebotMessage message) {
        CommandParseResult result = CommandParseResult.of(message.selfId(), message.messages());
        if (result.isCommand()) {
            LOGGER.debug("PrivateCommandEvent: Command parsed: {}", result.rawCommandLine());
            return new PrivateCommandEvent(message, result);
        }
        return new PrivateMessageEvent(message);
    }
    public PrivateCommandEvent(PrivateOnebotMessage message, CommandParseResult result) {
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
