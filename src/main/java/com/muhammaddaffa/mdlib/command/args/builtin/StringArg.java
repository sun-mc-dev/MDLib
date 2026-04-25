package com.muhammaddaffa.mdlib.command.args.builtin;

import com.muhammaddaffa.mdlib.command.args.ArgParseException;
import com.muhammaddaffa.mdlib.command.args.ArgumentType;
import com.muhammaddaffa.mdlib.command.args.TokenReader;
import org.bukkit.command.CommandSender;

public class StringArg implements ArgumentType<String> {

    @Override
    public String id() {
        return "<text>";
    }

    @Override
    public String parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String next = tokens.next();
        if (next == null || next.isEmpty()) {
            throw new ArgParseException("Expected " + id());
        }
        return next;
    }

}
