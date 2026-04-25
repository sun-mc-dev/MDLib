package com.muhammaddaffa.mdlib.command.args.builtin;

import com.muhammaddaffa.mdlib.command.args.ArgParseException;
import com.muhammaddaffa.mdlib.command.args.ArgumentType;
import com.muhammaddaffa.mdlib.command.args.TokenReader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

public final class WorldArg implements ArgumentType<World> {

    @Override
    public String id() {
        return "<world>";
    }

    @Override
    public World parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String name = tokens.next();
        if (name == null) throw new ArgParseException("Expected " + id());
        World w = Bukkit.getWorld(name);
        if (w == null) throw new ArgParseException("World '" + name + "' not found");
        return w;
    }

    @Override
    public List<String> suggestions(CommandSender sender, String prefix) {
        String p = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(p))
                .toList();
    }
}