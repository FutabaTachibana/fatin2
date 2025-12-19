package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.type.message.GroupOnebotMessage;

public class GroupCommandEvent extends GroupMessageEvent implements CommandEvent {
    private final String command;
    private final String[] args;

    public static GroupMessageEvent getCommandOrBasic(GroupOnebotMessage message) {
        // TODO: split command and args.
        if (message.parse().startsWith("/")) {
            String[] splits = message.parse().split(" ");
            String command = splits[0].substring(1);
            String[] args = new String[splits.length - 1];
            System.arraycopy(splits, 1, args, 0, splits.length - 1);
            return new GroupCommandEvent(message, command, args);
        }
        else {
            return new GroupMessageEvent(message);
        }
    }
    public GroupCommandEvent(GroupOnebotMessage message, String command, String[] args) {
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
