package org.f14a.fatin2.plugin.integrated;

import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.PermissionProvider;
import org.f14a.fatin2.type.Sender;
import org.f14a.fatin2.type.message.OnebotMessage;

import java.util.Set;

public class IntegratedPermissionProvider implements PermissionProvider {
    private final Set<Long> botAdmins;
    private final Set<Long> bannedUsers;
    public IntegratedPermissionProvider(Set<Long> botAdmins, Set<Long> banners) {
        this.botAdmins = botAdmins;
        this.bannedUsers = banners;
    }

    @Override
    public boolean hasPermission(CommandEvent event, int requiredPermission) {
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
        return permissionLevel >= requiredPermission;
    }
}
