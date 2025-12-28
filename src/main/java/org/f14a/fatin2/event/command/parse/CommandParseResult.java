package org.f14a.fatin2.event.command.parse;

import org.f14a.fatin2.model.message.Message;

import java.util.List;

public sealed interface CommandParseResult permits CommandParser.Result {
    boolean isCommand();
    boolean atBot();
    boolean hasReply();
    String command();
    String[] args();
    String rawCommandLine();

    static CommandParseResult of(long selfId, List<Message> messages) {
        return CommandParser.parse(selfId, messages);
    }
}
