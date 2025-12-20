package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class PrivateCommandEvent extends PrivateMessageEvent implements CommandEvent {
    private final CommandParser.Result result;

    public static PrivateMessageEvent getCommandOrBasic(PrivateOnebotMessage message) {
        CommandParser.Result result = CommandParser.parse(message.selfId(), message.messages());
        if (result.isCommand()) {
            EventBus.LOGGER.debug("PrivateCommandEvent: Command parsed: {}", result.rawCommandLine());
            return new PrivateCommandEvent(message, result);
        }
        return new PrivateMessageEvent(message);
    }
    public PrivateCommandEvent(PrivateOnebotMessage message, CommandParser.Result result) {
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
