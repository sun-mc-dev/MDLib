package com.muhammaddaffa.mdlib.command.commands;

import com.muhammaddaffa.mdlib.command.SenderType;
import com.muhammaddaffa.mdlib.command.args.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RoutedCommand implements SimpleCommandSpec {

    private final String name;
    private final String description;
    private final String explicitUsage;
    private final String permission;
    private final CommandPlan rootPlan = new CommandPlan(null);
    private final List<CommandPlan> subs = new ArrayList<>();
    private final List<String> rootAliases = new ArrayList<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> pendingConfirm = new ConcurrentHashMap<>();

    public RoutedCommand(String name) {
        this(name, null);
    }

    public RoutedCommand(String name, String permission) {
        this(name, "", "/" + name, permission);
    }

    protected RoutedCommand(String name, String description, String explicitUsage, String permission) {
        this.name = name;
        this.description = description != null ? description : "";
        this.explicitUsage = explicitUsage;
        this.permission = permission;
    }

    public RoutedCommand alias(List<String> names) {
        rootAliases.addAll(names);
        return this;
    }

    public RoutedCommand alias(String... names) {
        rootAliases.addAll(Arrays.asList(names));
        return this;
    }

    @Override
    public List<String> aliases() {
        return Collections.unmodifiableList(rootAliases);
    }

    protected CommandPlan root() {
        return rootPlan;
    }

    protected CommandPlan sub(String literal) {
        CommandPlan p = new CommandPlan(literal);
        subs.add(p);
        return p;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String permission() {
        return permission;
    }

    @Override
    public String usage() {
        if (explicitUsage != null && !explicitUsage.isEmpty()) return explicitUsage;
        if (rootPlan.isDefined()) return rootPlan.usageString(name);
        if (!subs.isEmpty()) return subs.getFirst().usageString(name);
        return "/" + name;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] raw) {
        try {
            if (raw.length == 0) {
                if (rootPlan.isDefined()) {
                    rootPlan.handle(sender, raw, permission, cooldowns, pendingConfirm);
                    return;
                }
                onRoot(sender);
                return;
            }

            String first = raw[0];

            CommandPlan matched = null;
            for (CommandPlan p : subs) {
                if (p.matchesToken(first)) {
                    matched = p;
                    break;
                }
            }

            if (matched != null) {
                String[] rest = Arrays.copyOfRange(raw, 1, raw.length);
                matched.handle(sender, rest, null, cooldowns, pendingConfirm);
                return;
            }

            if (rootPlan.isDefined()) {
                rootPlan.handle(sender, raw, permission, cooldowns, pendingConfirm);
                return;
            }
            onUnknownSub(sender, raw[0]);

        } catch (ArgParseException apx) {
            sender.sendMessage("§c" + apx.getMessage());
            sender.sendMessage("§7Usage: §f" + usage());
        } catch (Throwable t) {
            sender.sendMessage("§cAn internal error occurred. See console.");
            t.printStackTrace();
        }
    }

    protected boolean onRoot(CommandSender sender) {
        List<String> visible = new ArrayList<>();
        for (CommandPlan p : subs) {
            String perm = p.permission();
            if (perm == null || perm.isBlank() || sender.hasPermission(perm)) {
                List<String> labels = p.labelsForTab();
                String primary = labels.isEmpty() ? "" : labels.get(0);
                List<String> rest = labels.size() > 1 ? labels.subList(1, labels.size()) : List.of();
                if (rest.isEmpty()) visible.add(primary);
                else visible.add(primary + " §8(" + String.join(", ", rest) + ")§7");
            }
        }
        String list = visible.isEmpty() ? "-" : String.join("§7, §f", visible);
        sender.sendMessage("§7Available: §f" + list);
        sender.sendMessage("§7Usage: §f" + usage());
        return true;
    }

    protected boolean onUnknownSub(@NotNull CommandSender sender, String token) {
        sender.sendMessage("§cUnknown subcommand: §f" + token);
        return onRoot(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String @NotNull [] raw) {
        if (raw.length == 1) {
            String prefix = raw[0] == null ? "" : raw[0];
            List<String> candidates = new ArrayList<>();

            if (rootPlan.isDefined()) {
                String ph = rootPlan.firstParamPlaceholder();
                if (ph != null) candidates.add(ph);
            }

            for (CommandPlan p : subs) {
                String perm = p.permission();
                if (perm == null || perm.isBlank() || sender.hasPermission(perm)) {
                    var labels = p.labelsForTab();
                    if (!labels.isEmpty()) candidates.add(labels.get(0));
                }
            }

            List<String> out = new ArrayList<>(candidates.size());
            StringUtil.copyPartialMatches(prefix, candidates, out);
            return out;
        }

        if (raw.length == 0) {
            List<String> first = new ArrayList<>();
            String ph = rootPlan.isDefined() ? rootPlan.firstParamPlaceholder() : null;
            if (ph != null) first.add(ph);
            for (CommandPlan p : subs) {
                var labels = p.labelsForTab();
                if (!labels.isEmpty()) first.add(labels.getFirst());
            }
            return first;
        }

        CommandPlan matched = null;
        for (CommandPlan p : subs) {
            if (p.matchesToken(raw[0])) {
                matched = p;
                break;
            }
        }
        if (matched != null) {
            String[] rest = Arrays.copyOfRange(raw, 1, raw.length);
            return matched.tab(sender, rest);
        }

        if (rootPlan.isDefined()) return rootPlan.tab(sender, raw);
        return List.of();
    }

    @FunctionalInterface
    public interface Handler {
        void run(CommandSender sender, CommandContext ctx) throws Exception;
    }

    protected record Param(String name, ArgumentType<?> type, boolean optional, ArgSuggester suggester) {
    }

    public static final class CommandPlan {
        private final String literal;
        private final List<String> aliases = new ArrayList<>();
        private final List<Param> params = new ArrayList<>();
        private String permission;
        private Handler handler;
        private SenderType senderType = SenderType.ANY;
        private String playerOnlyMessage = "§cThis command can only be used by players.";
        private String consoleOnlyMessage = "§cThis command can only be used from console.";
        private long cooldownTicks = 0;
        private String cooldownMessage = "§cYou must wait before using this command again.";
        private boolean requireConfirm = false;
        private String confirmMessage = "§eRun the command again within 10 seconds to confirm.";

        public CommandPlan(String literal) {
            this.literal = literal;
        }

        public CommandPlan perm(String permission) {
            this.permission = permission;
            return this;
        }

        public String permission() {
            return permission;
        }

        public boolean isRoot() {
            return literal == null;
        }

        public boolean isDefined() {
            return handler != null;
        }

        public CommandPlan senderType(SenderType type) {
            this.senderType = type;
            return this;
        }

        public CommandPlan playerOnly() {
            return senderType(SenderType.PLAYER);
        }

        public CommandPlan consoleOnly() {
            return senderType(SenderType.CONSOLE);
        }

        public CommandPlan playerOnlyMessage(String msg) {
            this.playerOnlyMessage = msg;
            return this;
        }

        public CommandPlan consoleOnlyMessage(String msg) {
            this.consoleOnlyMessage = msg;
            return this;
        }

        public CommandPlan cooldown(long ticks) {
            this.cooldownTicks = ticks;
            return this;
        }

        public CommandPlan cooldownMessage(String msg) {
            this.cooldownMessage = msg;
            return this;
        }

        public CommandPlan requireConfirm() {
            this.requireConfirm = true;
            return this;
        }

        public CommandPlan confirmMessage(String msg) {
            this.confirmMessage = msg;
            return this;
        }

        public CommandPlan alias(List<String> names) {
            if (!isRoot()) aliases.addAll(names);
            return this;
        }

        public CommandPlan alias(String... names) {
            if (!isRoot()) aliases.addAll(Arrays.asList(names));
            return this;
        }

        public List<String> aliases() {
            return Collections.unmodifiableList(aliases);
        }

        public boolean matchesToken(String token) {
            if (isRoot()) return false;
            if (literal.equalsIgnoreCase(token)) return true;
            for (String a : aliases) if (a.equalsIgnoreCase(token)) return true;
            return false;
        }

        public CommandPlan arg(String name, ArgumentType<?> type, ArgSuggester suggester) {
            boolean opt = (type instanceof OptionalArg<?>);
            params.add(new Param(name, type, opt, suggester));
            return this;
        }

        public CommandPlan argOptional(String name, ArgumentType<?> type, ArgSuggester suggester) {
            ArgumentType<?> wrapped = (type instanceof OptionalArg<?>) ? type : OptionalArg.of(type);
            params.add(new Param(name, wrapped, true, suggester));
            return this;
        }

        public CommandPlan arg(String name, ArgumentType<?> type) {
            boolean opt = (type instanceof OptionalArg<?>);
            params.add(new Param(name, type, opt, null));
            return this;
        }

        public CommandPlan argOptional(String name, ArgumentType<?> type) {
            ArgumentType<?> wrapped = (type instanceof OptionalArg<?>) ? type : OptionalArg.of(type);
            params.add(new Param(name, wrapped, true, null));
            return this;
        }

        public CommandPlan exec(Handler handler) {
            this.handler = handler;
            return this;
        }

        boolean handle(CommandSender sender, String[] raw, String fallbackPerm,
                       Map<UUID, Long> cooldowns, Map<UUID, Long> pendingConfirm) throws Exception {
            String permToCheck = permission != null && !permission.isBlank()
                    ? permission
                    : (isRoot() ? fallbackPerm : null);
            if (permToCheck != null && !permToCheck.isBlank() && !sender.hasPermission(permToCheck)) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }

            if (senderType == SenderType.PLAYER && !(sender instanceof Player)) {
                sender.sendMessage(playerOnlyMessage);
                return true;
            }
            if (senderType == SenderType.CONSOLE && sender instanceof Player) {
                sender.sendMessage(consoleOnlyMessage);
                return true;
            }

            if (sender instanceof Player p && cooldownTicks > 0) {
                UUID uuid = p.getUniqueId();
                long now = System.currentTimeMillis();
                long cooldownMs = cooldownTicks * 50L;
                if (cooldowns.containsKey(uuid)) {
                    long elapsed = now - cooldowns.get(uuid);
                    if (elapsed < cooldownMs) {
                        sender.sendMessage(cooldownMessage);
                        return true;
                    }
                }
                cooldowns.put(uuid, now);
            }

            if (requireConfirm && sender instanceof Player p) {
                UUID uuid = p.getUniqueId();
                long now = System.currentTimeMillis();
                if (!pendingConfirm.containsKey(uuid) || now - pendingConfirm.get(uuid) > 10_000) {
                    pendingConfirm.put(uuid, now);
                    sender.sendMessage(confirmMessage);
                    return true;
                }
                pendingConfirm.remove(uuid);
            }

            if (handler == null) return true;

            TokenReader tr = new TokenReader(raw);
            CommandContext ctx = new CommandContext();

            for (Param p : params) {
                if (!tr.hasNext() && !p.type.greedy()) {
                    if (p.optional) {
                        ctx.put(p.name, null);
                        continue;
                    }
                    throw new ArgParseException("Missing " + p.type.id());
                }
                Object parsed = p.type.parse(sender, tr);
                ctx.put(p.name, parsed);
            }
            handler.run(sender, ctx);
            return true;
        }

        List<String> tab(CommandSender sender, String[] raw) {
            if (params.isEmpty()) return List.of();

            int argIndex = Math.max(0, raw.length - 1);
            if (argIndex >= params.size()) return List.of();

            PartialContext prev = new PartialContext();
            TokenReader tr = new TokenReader(raw);
            for (int i = 0; i < argIndex; i++) {
                Param p = params.get(i);
                if (!tr.hasNext() && !p.type.greedy()) {
                    if (p.optional) {
                        prev.put(p.name(), null);
                        continue;
                    }
                    return List.of();
                }
                if (p.type.greedy()) {
                    p.type.parse(sender, tr);
                    return List.of();
                }
                try {
                    Object parsed = p.type.parse(sender, tr);
                    prev.put(p.name(), parsed);
                } catch (Exception e) {
                    return List.of();
                }
            }

            Param current = params.get(argIndex);
            String prefix = raw.length == 0 ? "" : raw[raw.length - 1];

            List<String> candidates = current.suggester() != null
                    ? current.suggester().suggest(sender, prefix, prev)
                    : current.type().suggestions(sender, prefix);

            if (candidates == null || candidates.isEmpty()) return List.of();

            List<String> out = new ArrayList<>(candidates.size());
            StringUtil.copyPartialMatches(prefix == null ? "" : prefix, candidates, out);
            return out;
        }

        public String usageString(String rootLabel) {
            StringBuilder sb = new StringBuilder("/").append(rootLabel);
            if (!isRoot()) sb.append(' ').append(literal);
            for (Param p : params) {
                String id = p.type.id();
                if (p.optional) id = "[" + id.replaceAll("[\\[\\]<>]", "") + "]";
                sb.append(' ').append(id);
            }
            return sb.toString();
        }

        public List<String> labelsForTab() {
            if (isRoot()) return List.of();
            List<String> out = new ArrayList<>(1 + aliases.size());
            out.add(literal);
            out.addAll(aliases);
            return out;
        }

        String firstParamPlaceholder() {
            if (params.isEmpty()) return null;
            Param p = params.getFirst();
            String name = p.name();
            return p.optional() ? "[" + name + "]" : "<" + name + ">";
        }
    }
}