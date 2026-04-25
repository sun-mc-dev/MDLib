package com.muhammaddaffa.mdlib.command.args.builtin;

import com.muhammaddaffa.mdlib.command.args.ArgParseException;
import com.muhammaddaffa.mdlib.command.args.ArgumentType;
import com.muhammaddaffa.mdlib.command.args.TokenReader;
import org.bukkit.command.CommandSender;

public final class IntArg implements ArgumentType<Integer> {

    @Override
    public String id() {
        return "<int>";
    }

    @Override
    public Integer parse(CommandSender s, TokenReader t) {
        String raw = t.next();
        if (raw == null) throw new ArgParseException("Expected " + id());
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new ArgParseException("Expected <int>, got '" + raw + "'");
        }
    }

}
