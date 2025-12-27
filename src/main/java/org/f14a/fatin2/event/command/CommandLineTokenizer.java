package org.f14a.fatin2.event.command;

import org.f14a.fatin2.exception.CommandSyntaxException;

import java.util.ArrayList;
import java.util.List;

final class CommandLineTokenizer {
    private CommandLineTokenizer() {
    }
    /**
     * Tokenize a command line string into tokens.
     * Example: hello "a b" c\"d -> ["hello", "a b", "c\"d"]
     */
    public static List<String> tokenize(String input) {
        if (input == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        StringBuilder cursor = new StringBuilder();
        boolean inQuotes = false; // Handle quotes
        boolean escaping = false; // Handle backslash escapes
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (escaping) {
                // minimal escapes; you can extend if desired
                switch (ch) {
                    case '"' -> cursor.append('"');
                    case '\\' -> cursor.append('\\');
                    case 'n' -> cursor.append('\n');
                    case 't' -> cursor.append('\t');
                    default -> cursor.append('\\').append(ch); // unknown escape: treat literally
                }
                escaping = false;
                continue;
            }
            // Start escape
            if (ch == '\\') {
                escaping = true;
                continue;
            }
            // Turn on/off quotes
            if (ch == '"') {
                inQuotes = !inQuotes;
                continue; // do not include quotes
            }
            if (!inQuotes && Character.isWhitespace(ch)) {
                // token boundary
                if (!cursor.isEmpty()) {
                    out.add(cursor.toString());
                    cursor.setLength(0);
                }
                continue;
            }
            cursor.append(ch);
        }
        if (escaping) {
            // trailing backslash; treat as syntax error (or append backslash)
            throw new CommandSyntaxException("Invalid command line: trailing escape '\\\\' in: " + input);
        }
        if (inQuotes) {
            throw new CommandSyntaxException("Invalid command line: missing closing quote in: " + input);
        }
        if (!cursor.isEmpty()) {
            out.add(cursor.toString());
        }
        return out;
    }

}
