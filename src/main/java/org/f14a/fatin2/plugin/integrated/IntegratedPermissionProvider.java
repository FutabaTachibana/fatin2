package org.f14a.fatin2.plugin.integrated;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.CommandEventListener;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.command.PermissionDeniedEvent;
import org.f14a.fatin2.plugin.Plugin;
import org.f14a.fatin2.model.message.Sender;
import org.f14a.fatin2.exception.PluginInternalException;
import org.f14a.fatin2.model.message.OnebotMessage;
import org.f14a.fatin2.api.generator.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IntegratedPermissionProvider implements Plugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegratedPermissionProvider.class);
    private static final String PERM = "perm.yml";

    private static final String KEY_BOT_ADMINS = "bot_admins";
    private static final String KEY_BANNED_USERS = "banned_users";

    private final Set<Long> botAdmins = new HashSet<>();
    private final Set<Long> botAdminsAdded = new HashSet<>();
    private final Set<Long> botAdminsRemoved = new HashSet<>();
    private final Set<Long> bannedUsers = new HashSet<>();
    private final Set<Long> bannedUsersAdded = new HashSet<>();
    private final Set<Long> bannedUsersRemoved = new HashSet<>();

    public boolean hasPermission(CommandEvent event, CommandEventListener listener) {
        OnebotMessage message = event.getMessage();
        long userId = message.userId();
        int permissionLevel = 0;
        if (botAdmins.contains(userId)) {
            permissionLevel = 4;
        } else if (bannedUsers.contains(userId)) {
            permissionLevel = -1;
        } else {
            Sender sender = message.sender();
            String role = sender != null ? sender.role() : null;
            if ("owner".equals(role)) {
                permissionLevel = 3;
            } else if ("admin".equals(role)) {
                permissionLevel = 2;
            }
        }
        return permissionLevel >= listener.permission();
    }

    @Override
    public void onLoad() {
        Path perm = ensurePermFileExists();
        // Load perm.yml once. Later changes are kept in memory and flushed onDisable().
        try (InputStream in = Files.newInputStream(perm)) {
            Yaml yaml = new Yaml();
            Map<String, Object> permData = yaml.load(in);
            if (permData != null) {
                List<Long> admins = readLongList(permData, KEY_BOT_ADMINS);
                if (admins == null) {
                    admins = readLongList(permData, KEY_BOT_ADMINS);
                }
                if (admins != null) {
                    botAdmins.addAll(admins);
                }

                List<Long> banned = readLongList(permData, KEY_BANNED_USERS);
                if (banned != null) {
                    bannedUsers.addAll(banned);
                }
            }
        } catch (Exception e) {
            throw new PluginInternalException("Failed to load perm.yml from " + perm.toAbsolutePath(), e);
        }
    }

    @Override
    public void onEnable() {
        EventBus.getInstance().registerPermissionProvider(this::hasPermission);
        EventBus.getInstance().register(this, this);
    }

    @Override
    public void onDisable() {
        // Unregister first to avoid handling new commands while we're flushing.
        EventBus.getInstance().unregisterPermissionProvider();

        // Flush any permission changes back to perm.yml.
        // We intentionally do it at disable/unload time to reduce file IO.
        try {
            flushPermChanges();
        } catch (Exception e) {
            // Disable should not crash the whole bot; log and continue.
            LOGGER.error("Failed to flush perm.yml", e);
        }

        // Reset deltas to avoid accidental double-apply if the plugin instance is reused.
        clearDeltas();
    }

    @Override
    public String getName() {
        return "integrated-permission";
    }

    @Override
    public String getDisplayName() {
        return "Integrated Permission";
    }

    @Override
    public String getVersion() {
        return "integrated";
    }

    @Override
    public String getAuthor() {
        return "Fatin2";
    }

    @Override
    public String getDescription() {
        return "内置的权限管理插件。";
    }

    @EventHandler
    public void onPermissionDenied(PermissionDeniedEvent event) {
        event.getCurrentEvent().send(MessageGenerator.text("你没有执行此命令的权限"));
    }

    @OnCommand(command = "list_admins", description = "列出机器人管理员")
    public void onListAdmins(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("机器人管理员列表:\n");
        for (Long adminId : botAdmins) {
            sb.append("- ").append(adminId).append("\n");
        }
        String string = sb.toString().trim();
        if (string.length() > 1000) {
            event.setSendForward(true);
        }
        event.send(MessageGenerator.text(string));
    }

    @OnCommand(command = "list_banned", description = "列出被封禁用户")
    public void onListBannedUsers(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("被封禁用户列表:\n");
        for (Long bannedId : bannedUsers) {
            sb.append("- ").append(bannedId).append("\n");
        }
        String string = sb.toString().trim();
        if (string.length() > 1000) {
            event.setSendForward(true);
        }
        event.send(MessageGenerator.text(string));
    }

    @OnCommand(command = "set_admin", permission = 4, description = "设置机器人管理员")
    public void onSetAdmin(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length < 1) {
            event.send(MessageGenerator.text("请提供用户ID"));
            return;
        }
        try {
            long userId = Long.parseLong(args[0]);
            if (botAdmins.add(userId)) {
                botAdminsAdded.add(userId);
                botAdminsRemoved.remove(userId);
                event.send(MessageGenerator.text("已将用户 " + userId + " 设置为机器人管理员"));
            } else {
                event.send(MessageGenerator.text("用户 " + userId + " 已是机器人管理员"));
            }
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("无效的用户ID"));
        }
    }

    @OnCommand(command = "unset_admin", permission = 4, description = "取消机器人管理员")
    public void onUnsetAdmin(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length < 1) {
            event.send(MessageGenerator.text("请提供用户ID"));
            return;
        }
        try {
            long userId = Long.parseLong(args[0]);
            if (botAdmins.remove(userId)) {
                botAdminsRemoved.add(userId);
                botAdminsAdded.remove(userId);
                event.send(MessageGenerator.text("已取消用户 " + userId + " 的机器人管理员权限"));
            } else {
                event.send(MessageGenerator.text("用户 " + userId + " 不是机器人管理员"));
            }
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("无效的用户ID"));
        }
    }

    @OnCommand(command = "ban_user", permission = 2, description = "封禁用户")
    public void onBanUser(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length < 1) {
            event.send(MessageGenerator.text("请提供用户ID"));
            return;
        }
        try {
            long userId = Long.parseLong(args[0]);
            if (bannedUsers.add(userId)) {
                bannedUsersAdded.add(userId);
                bannedUsersRemoved.remove(userId);
                event.send(MessageGenerator.text("已封禁用户 " + userId));
            } else {
                event.send(MessageGenerator.text("用户 " + userId + " 已被封禁"));
            }
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("无效的用户ID"));
        }
    }

    @OnCommand(command = "unban_user", permission = 2, description = "解除封禁")
    public void onUnbanUser(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length < 1) {
            event.send(MessageGenerator.text("请提供用户ID"));
            return;
        }
        try {
            long userId = Long.parseLong(args[0]);
            if (bannedUsers.remove(userId)) {
                bannedUsersRemoved.add(userId);
                bannedUsersAdded.remove(userId);
                event.send(MessageGenerator.text("已解除封禁用户 " + userId));
            } else {
                event.send(MessageGenerator.text("用户 " + userId + " 未被封禁"));
            }
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("无效的用户ID"));
        }
    }

    private Path ensurePermFileExists() {
        Path permPath = Path.of(PERM);
        if (!Files.exists(permPath)) {
            // Create a default perm.yml from resources.
            // This only happens on first run.
            Path parent = permPath.getParent();
            try (InputStream in = IntegratedPermissionProvider.class.getResourceAsStream("/" + PERM)) {
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                if (in == null) {
                    throw new IOException("Default perm template perm.yml not found in resources");
                }
                Files.copy(in, permPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new PluginInternalException("Failed to create default perm.yml at " + permPath.toAbsolutePath(), e);
            }
        }
        return  permPath;
    }
    /**
     * Read a YAML list and convert items to Long.
     * <p>
     * SnakeYAML sometimes returns Integer / Long / String depending on content.
     * We normalize it here.
     */
    private static List<Long> readLongList(Map<String, Object> map, String key) {
        Object raw = map.get(key);
        if (raw == null) {
            return null;
        }
        if (!(raw instanceof List<?> rawList)) {
            return null;
        }

        List<Long> result = new ArrayList<>(rawList.size());
        for (Object o : rawList) {
            if (o == null) {
                continue;
            }
            if (o instanceof Number n) {
                result.add(n.longValue());
            } else {
                // fall back to parsing string
                try {
                    result.add(Long.parseLong(String.valueOf(o)));
                } catch (NumberFormatException ignored) {
                    // ignore invalid entries
                }
            }
        }
        return result;
    }
    /**
     * Merge in-memory permissions into perm.yml.
     * <p>
     * Implementation detail:
     * - We write full yaml to disk (not just append diffs).
     * - This ensures the file always reflects the current effective state.
     * - Changes are rare (admin/ban operations), so a full rewrite is acceptable.
     */
    private void flushPermChanges() {
        // Fast path: nothing changed.
        if (botAdminsAdded.isEmpty() && botAdminsRemoved.isEmpty() && bannedUsersAdded.isEmpty() && bannedUsersRemoved.isEmpty()) {
            return;
        }
        Path permPath = ensurePermFileExists();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put(KEY_BOT_ADMINS, new ArrayList<>(botAdmins));
        out.put(KEY_BANNED_USERS, new ArrayList<>(bannedUsers));

        // Dump as block style so YAML lists are stored as:
        // key:
        //   - item1
        //   - item2
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        String dumped = yaml.dump(out);

        // Atomic(ish) replace: write to temp file then move/replace.
        Path tmp = permPath.resolveSibling(permPath.getFileName() + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(dumped);
        } catch (IOException e) {
            throw new PluginInternalException("Failed to write temp perm.yml: " + tmp, e);
        }

        try {
            Files.move(tmp, permPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // ATOMIC_MOVE may fail on some filesystems; retry without it.
            try {
                Files.move(tmp, permPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e2) {
                throw new PluginInternalException("Failed to replace perm.yml: " + permPath, e2);
            }
        }

        LOGGER.info("Flushed perm.yml changes to {} (admins={}, banned={})", permPath.toAbsolutePath(), botAdmins.size(), bannedUsers.size());
    }

    private void clearDeltas() {
        botAdminsAdded.clear();
        botAdminsRemoved.clear();
        bannedUsersAdded.clear();
        bannedUsersRemoved.clear();
    }
}
