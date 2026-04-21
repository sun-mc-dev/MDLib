package com.muhammaddaffa.mdlib.commands.args.builtin;

import com.muhammaddaffa.mdlib.commands.args.ArgParseException;
import com.muhammaddaffa.mdlib.commands.args.ArgumentType;
import com.muhammaddaffa.mdlib.commands.args.TokenReader;
import org.bukkit.command.CommandSender;

public final class RangedDoubleArg implements ArgumentType<Double> {

    private final double min;
    private final double max;

    public RangedDoubleArg(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String id() {
        return "<double(" + min + "-" + max + ")>";
    }

    @Override
    public Double parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String raw = tokens.next();
        if (raw == null) throw new ArgParseException("Expected " + id());
        try {
            double val = Double.parseDouble(raw);
            if (val < min || val > max)
                throw new ArgParseException("Value must be between " + min + " and " + max);
            return val;
        } catch (NumberFormatException e) {
            throw new ArgParseException("Expected a number, got '" + raw + "'");
        }
    }
}