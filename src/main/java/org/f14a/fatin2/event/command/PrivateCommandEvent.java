package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class PrivateCommandEvent extends PrivateMessageEvent implements CommandEvent {
    private final String command;
    private final String[] args;

    public static PrivateMessageEvent getCommandOrBasic(PrivateOnebotMessage message) {
        if (message.parse().startsWith("/")) {
            String[] splits = message.parse().split(" ");
            String command = splits[0].substring(1);
            String[] args = new String[splits.length - 1];
            System.arraycopy(splits, 1, args, 0, splits.length - 1);
            return new PrivateCommandEvent(message, command, args);
        }
        else {
            return new PrivateMessageEvent(message);
        }
    }
    public PrivateCommandEvent(PrivateOnebotMessage message, String command, String[] args) {
        super(message);
        this.command = command;
        this.args = args;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String[] getArgs() {
        return args;
    }
}
