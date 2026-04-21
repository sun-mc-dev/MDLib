package com.muhammaddaffa.mdlib.commands.args.builtin;

import com.muhammaddaffa.mdlib.commands.args.ArgParseException;
import com.muhammaddaffa.mdlib.commands.args.ArgumentType;
import com.muhammaddaffa.mdlib.commands.args.TokenReader;
import org.bukkit.command.CommandSender;

public final class RangedIntArg implements ArgumentType<Integer> {

    private final int min;
    private final int max;

    public RangedIntArg(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String id() {
        return "<int(" + min + "-" + max + ")>";
    }

    @Override
    public Integer parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String raw = tokens.next();
        if (raw == null) throw new ArgParseException("Expected " + id());
        try {
            int val = Integer.parseInt(raw);
            if (val < min || val > max)
                throw new ArgParseException("Value must be between " + min + " and " + max);
            return val;
        } catch (NumberFormatException e) {
            throw new ArgParseException("Expected a number, got '" + raw + "'");
        }
    }
}