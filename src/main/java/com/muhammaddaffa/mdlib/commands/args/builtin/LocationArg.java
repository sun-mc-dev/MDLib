package com.muhammaddaffa.mdlib.commands.args.builtin;

import com.muhammaddaffa.mdlib.commands.args.ArgParseException;
import com.muhammaddaffa.mdlib.commands.args.ArgumentType;
import com.muhammaddaffa.mdlib.commands.args.TokenReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LocationArg implements ArgumentType<Location> {

    @Override
    public String id() {
        return "<x> <y> <z>";
    }

    @Override
    public Location parse(CommandSender sender, TokenReader tokens) throws ArgParseException {
        String xs = tokens.next();
        String ys = tokens.next();
        String zs = tokens.next();
        if (xs == null || ys == null || zs == null) throw new ArgParseException("Expected <x> <y> <z>");
        try {
            double x = parseCoord(xs, sender instanceof Player p ? p.getLocation().getX() : 0);
            double y = parseCoord(ys, sender instanceof Player p ? p.getLocation().getY() : 0);
            double z = parseCoord(zs, sender instanceof Player p ? p.getLocation().getZ() : 0);
            World world = sender instanceof Player p ? p.getWorld() : Bukkit.getWorlds().getFirst();
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            throw new ArgParseException("Invalid coordinates: " + xs + " " + ys + " " + zs);
        }
    }

    private double parseCoord(String s, double relative) {
        if (s.startsWith("~")) {
            if (s.length() == 1) return relative;
            return relative + Double.parseDouble(s.substring(1));
        }
        return Double.parseDouble(s);
    }
}