package com.muhammaddaffa.mdlib.commands.args.builtin;

import com.muhammaddaffa.mdlib.commands.args.ArgParseException;
import com.muhammaddaffa.mdlib.commands.args.ArgumentType;
import com.muhammaddaffa.mdlib.commands.args.TokenReader;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public final class UUIDArg implements ArgumentType<UUID> {

    @Override
    public String id() {
        return "<uuid>";
    }

    @Override
    public UUID parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String raw = tokens.next();
        if (raw == null) throw new ArgParseException("Expected " + id());
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new ArgParseException("Invalid UUID: '" + raw + "'");
        }
    }
}