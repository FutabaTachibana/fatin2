package org.f14a.fatin2.event.command;

import org.f14a.fatin2.type.message.OnebotMessage;

public interface PermissionProvider {
    /**
     * Return true if the message has permission to execute the command.
     */
    boolean hasPermission(OnebotMessage message, int requiredPermission);
}
