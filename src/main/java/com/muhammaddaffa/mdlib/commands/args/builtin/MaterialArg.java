package com.muhammaddaffa.mdlib.commands.args.builtin;

import com.muhammaddaffa.mdlib.commands.args.ArgParseException;
import com.muhammaddaffa.mdlib.commands.args.ArgumentType;
import com.muhammaddaffa.mdlib.commands.args.TokenReader;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class MaterialArg implements ArgumentType<Material> {

    @Override
    public String id() {
        return "<material>";
    }

    @Override
    public Material parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String raw = tokens.next();
        if (raw == null) throw new ArgParseException("Expected " + id());
        Material mat = Material.matchMaterial(raw);
        if (mat == null) throw new ArgParseException("Unknown material: '" + raw + "'");
        return mat;
    }

    @Override
    public List<String> suggestions(CommandSender sender, String prefix) {
        String p = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return Arrays.stream(Material.values())
                .map(m -> m.name().toLowerCase(Locale.ROOT))
                .filter(n -> n.startsWith(p))
                .toList();
    }
}