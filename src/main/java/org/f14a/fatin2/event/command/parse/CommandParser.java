package org.f14a.fatin2.event.command.parse;

import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.model.message.Message;
import org.f14a.fatin2.exception.OnebotProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Parse OneBot message segments (Message[]) into command invocation.
 * <p>
 * This parser is protocol-aware:
 * <ul>
 * <li> It can ignore leading [reply] segments</li>
 * <li> It can ignore leading [at] segments and detect whether @selfId occurred</li>
 * <li> It can ignore leading whitespace-only text segments</li>
 * </ul>
 * It does NOT depend on Message.parse() (display text), so it is stable against formatting changes.
 */
final class CommandParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandParser.class);
    private CommandParser() {
    }
    public record Result(
            boolean isCommand,
            boolean atBot,
            boolean hasReply,
            String command,
            String[] args,
            String rawCommandLine
    ) implements CommandParseResult {}
    public static Result parse(long selfId, List<Message> segments) {
        if (segments == null || segments.isEmpty()) {
            return new Result(false, false, false, "", new String[0], "");
        }
        PrefixScan scan = scanPrefixes(selfId, segments);
        String commandLine = scan.remainingText.trim();
        String prefix = Config.getConfig().getCommandPrefix();
        LOGGER.debug("CommandParser: after prefix scan: atBot={}, hasReply={}, remaining={}", scan.atBot, scan.hasReply, commandLine);
        if (!commandLine.startsWith(prefix)) {
            // does not start with prefix => not a command
            LOGGER.debug("CommandParser: command prefix {} not found.", prefix);
            return new Result(false, scan.atBot, scan.hasReply, "", new String[0], commandLine);
        }
        String withoutPrefix = commandLine.substring(prefix.length()).trim();
        if (withoutPrefix.isEmpty()) {
            // "/" only => not a valid command
            LOGGER.debug("CommandParser: command prefix only, no command found.");
            return new Result(false, scan.atBot, scan.hasReply, "", new String[0], commandLine);
        }
        CommandLine cl = parseCommandLine(withoutPrefix);
        return new Result(true, scan.atBot, scan.hasReply, cl.command, cl.args, withoutPrefix);
    }
    /** Holds prefix scan result. */
    private record PrefixScan(boolean atBot, boolean hasReply, String remainingText) {}
    /**
     * Strip leading reply/at/whitespace text segments.
     * Then concatenate subsequent text segments as remainingText. <br>
     * If non-text segments appear before any meaningful text and allowNonTextAfterPrefix=false,
     * we treat it as not a command (remainingText becomes empty).
     */
    private static PrefixScan scanPrefixes(long selfId, List<Message> segments) {
        boolean atBot = false;
        boolean hasReply = false;
        int index = 0;
        // Consume leading reply / at / whitespace text
        while (index < segments.size()) {
            Message seg = segments.get(index);
            String type = seg.type();
            switch (type) {
                case "reply" -> {
                    hasReply = true;
                    index++;
                }
                case "at" -> {
                    String qq = requireDataString(seg, "qq");
                    if (qq.equals(String.valueOf(selfId))) {
                        atBot = true;
                    }
                    index++;
                }
                case "text" -> {
                    String text = requireDataString(seg, "text");
                    if (!text.trim().isEmpty()) {
                        // Find meaningful text, stop here.
                        break;
                    }
                    else {
                        index++;
                    }
                }
            }
            // If we broke from switch by "break" inside case "text"/default, we need to stop outer while too.
            if (index < segments.size()) {
                String t = segments.get(index).type();
                if ("text".equals(t) && !requireDataString(segments.get(index), "text").trim().isEmpty()) {
                    break;
                }
            }
        }
        // Build remaining text from index...end: concatenate text segments only.
        String remaining = concatTextSegments(segments.stream().skip(index).toList());
        return new PrefixScan(atBot, hasReply, remaining);
    }
    private static String concatTextSegments(List<Message> segments) {
        StringBuilder sb = new StringBuilder();
        for (Message seg : segments) {
            if (seg == null) continue;
            if ("text".equals(seg.type())) {
                String text = requireDataString(seg, "text");
                sb.append(text);
            }
        }
        return sb.toString();
    }
    private static CommandLine parseCommandLine(String string) {
        // minimal parser: split by whitespace, no quotes support
        // You can upgrade later to support quoted args.
        String remaining = string.trim();
        if (remaining.isEmpty()) {
            return new CommandLine("", new String[0]);
        }
        List<String> tokens = CommandLineTokenizer.tokenize(remaining);
        String command = tokens.getFirst();
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);
        return new CommandLine(command, args);
    }
    private record CommandLine(String command, String[] args) {}
    private static String requireDataString(Message segment, String key) {
        try {
            Object v = segment.data().get(key);
            if (v == null) {
                throw new OnebotProtocolException("Missing data field '" + key
                        + "' for segment type=" + segment.type() + ", data=" + segment.data());
            }
            return v.toString();
        } catch (RuntimeException e) {
            // includes NPE, ClassCastException etc.
            throw new OnebotProtocolException("Malformed segment data for key '"
                    + key + "': type=" + segment.type() + ", data=" + segment.data(), e);
        }
    }
}
