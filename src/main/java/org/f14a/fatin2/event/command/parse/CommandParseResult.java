package org.f14a.fatin2.event.command.parse;

import org.f14a.fatin2.model.Message;

public interface CommandParseResult {
    boolean isCommand();
    boolean atBot();
    boolean hasReply();
    String command();
    String[] args();
    String rawCommandLine();

    static CommandParseResult of(long selfId, Message[] segments) {
        return CommandParser.parse(selfId, segments);
    }
}
